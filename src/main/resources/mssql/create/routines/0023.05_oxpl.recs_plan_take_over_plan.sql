create or alter procedure oxpl.recs_plan_take_over_plan
    @coarea char(4),
    @from_set int,
    @dest_set int,
    @kid varchar(20)
as
    begin
        set nocount on;
        begin try
            if exists (select * from oxpl.tbl_int_recs_plan_head as a
                        inner join oxpl.tbl_int_recs_plan_vals as b
                        on a.id = b.head_id
                        inner join oxpl.tbl_int_data_set_per as c
                        on b.data_set = c.data_set and b.an = c.an and b.per = c.per
                        where a.data_set = @dest_set and a.coarea = @coarea and c.actual = 0)
                raiserror('Există deja valori planificate pe setul de date în scop!',16, 1);

            /* verificare numar pozitii documente */
            if exists (select a.cost_centre, a.opex_categ, a.ic_part from oxpl.tbl_int_recs_plan_head as a
                        where a.data_set = @dest_set and a.coarea = @coarea
                        group by a.cost_centre, a.opex_categ, a.ic_part
                        having count(*) > 1)
                raiserror('Există mai mult de un document per combinaţie de: centru de cost + categorie cheltuieli + partener intercompanie!', 16, 1);

            /* stabilire ani calcul */
            declare @ani_calcul table (an smallint not null, has_actual bit not null);
            insert into @ani_calcul (an, has_actual)
            select
                a.an, a.actual
            from (select distinct
                    m.an,
                    first_value(m.actual) over (partition by m.an order by m.actual desc rows between unbounded preceding and unbounded following) as actual,
                    first_value(m.actual) over (partition by m.an order by m.actual asc rows between  unbounded preceding and unbounded following) as planned
                from oxpl.tbl_int_data_set_per as m
                where m.data_set = @dest_set) as a
            inner join (select distinct an from oxpl.tbl_int_data_set_per where data_set = @from_set) as b
            on a.an = b.an
            where a.planned = 0;

            if not exists (select * from @ani_calcul)
                raiserror('Nu există ani comuni în perioadele celor 2 seturi de date: sursă şi destinatar!',16, 1);

            if exists (select *
                        from (select m.* from oxpl.tbl_int_data_set_per as m
                                inner join @ani_calcul as n
                                on m.an = n.an
                                where m.data_set = @from_set) as a               
                        left join (select * from oxpl.tbl_int_data_set_per where data_set = @dest_set) as b
                        on a.an = b.an and a.per = b.per
                        where b.per is null)
                raiserror('Nu se regăsesc toate perioadele din setul de date sursă în cel destinatar!',16, 1);

            /* stabilire perioade de calcul */
            declare @per_calcul table (an smallint not null, per char(2) not null, actual bit not null);
            insert into @per_calcul(an, per, actual)
            select
                a.an, a.per, a.actual
            from oxpl.tbl_int_data_set_per as a
            inner join @ani_calcul as b
            on a.an = b.an
            where a.data_set = @dest_set;

            /* verificare existenta centre de cost */
            if exists (select * from oxpl.tbl_int_recs_plan_head as a
                        inner join oxpl.tbl_int_recs_plan_vals as b
                        on a.id = b.head_id
                        inner join @per_calcul as c
                        on b.an = c.an and b.per = c.per
                        left join (select m.* from oxpl.tbl_int_ccntr as m
                                    inner join oxpl.tbl_int_coarea as n
                                    on m.hier = n.cc_hier
                                    where m.data_set = @dest_set and n.cod = @coarea) as d
                        on a.hier = d.hier and a.cost_centre = d.cod
                        where a.coarea = @coarea and a.data_set = @from_set and d.cod is null)
                raiserror('Nu se regăsesc toate centrele de cost cu înregistrări din setul de date sursă în ierarhia celui destinatar!',16, 1);

            drop table if exists #oxpl_recs_sursa;
            create table #oxpl_recs_sursa(
                id uniqueidentifier not null default newsequentialid(),
                descr nvarchar(2000),
                cost_centre varchar(10) not null,
                opex_categ int not null,
                ic_part varchar(5),
                valori nvarchar(max) not null,
                diferenta nvarchar(max)
            );

            /* pregatire valori de preluat */
            with recs as (
                select
                    a.descr,
                    a.cost_centre,
                    a.opex_categ,
                    a.ic_part,
                    c.an,
                    c.per,
                    sum(b.valoare) as valoare
                from oxpl.tbl_int_recs_plan_head as a
                inner join oxpl.tbl_int_recs_plan_vals as b
                on a.id = b.head_id
                inner join @per_calcul as c
                on b.an = c.an and b.per = c.per
                where a.data_set = @from_set and a.coarea = @coarea
                group by a.descr, a.cost_centre, a.opex_categ, a.ic_part, c.an, c.per
            )
            insert into #oxpl_recs_sursa(descr, cost_centre, opex_categ, ic_part, valori)
            select
                a.descr,
                a.cost_centre,
                a.opex_categ,
                a.ic_part,
                (select
                    p.an,
                    p.per,
                    coalesce(q.valoare, 0) as valoare
                from @per_calcul as p
                left join (select
                                n.an,
                                n.per,
                                sum(n.valoare) as valoare
                            from recs as n
                            where n.cost_centre = a.cost_centre and n.opex_categ = a.opex_categ and (n.ic_part = a.ic_part or (n.ic_part is null and a.ic_part is null))
                            group by n.an, n.per) as q
                on p.an = q.an and p.per = q.per
                order by p.an asc, p.per asc
                for json path) as valori
            from (select
                    substring(string_agg(m.descr, '; ') within group (order by m.cost_centre asc, m.opex_categ asc, m.ic_part asc), 1, 2000) as descr,
                    m.cost_centre,
                    m.opex_categ,
                    m.ic_part
                from (select distinct descr, cost_centre, opex_categ, ic_part from recs) as m
                group by m.cost_centre, m.opex_categ, m.ic_part) as a
            order by a.cost_centre asc, a.opex_categ asc, a.ic_part asc;

            /* calculare diferenta */
            update a
            set diferenta = (
                select
                    q.an,
                    (q.plan_val - coalesce(p.act_val, 0)) as valoare
                from (select
                        a1.an,
                        sum(a1.valoare) as plan_val
                    from openjson(a.valori) with (an smallint '$.an', per char(2) '$.per', valoare float '$.valoare') as a1
                    inner join (select * from @per_calcul where actual = 1) as a2
                    on a1.an = a2.an and a1.per = a2.per
                    group by a1.an) as q
                left join (select
                                b2.an,
                                sum(b2.valoare) as act_val
                            from oxpl.tbl_int_recs_plan_head as b1
                            inner join oxpl.tbl_int_recs_plan_vals as b2
                            on b1.id = b2.head_id
                            inner join (select * from @per_calcul where actual = 1) as b3
                            on b2.an = b3.an and b2.per = b3.per
                            where b1.data_set = @dest_set and b1.cost_centre = a.cost_centre and b1.opex_categ = a.opex_categ and
                                    (b1.ic_part = a.ic_part or (b1.ic_part is null and a.ic_part is null))
                            group by b2.an) as p
                on q.an = p.an
                order by q.an asc
                for json path)
            from #oxpl_recs_sursa as a;

            /* aplicare diferente la valorile din perioadele de planificare 
            update a
            set a.valori = (
                select * from 
            )
            from #oxpl_recs_sursa as a */
        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            throw;
        end catch
    end;