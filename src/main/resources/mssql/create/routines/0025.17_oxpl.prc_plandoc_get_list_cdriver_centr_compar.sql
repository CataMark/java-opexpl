create or alter procedure oxpl.prc_plandoc_get_list_cdriver_centr_compar
    @main_set int,
    @compar_set int,
    @hier char(5),
    @cdriver char(5),
    @ocateg int = null
as
    begin
        set nocount on;

        if @ocateg is null /* versiune fara categorie cheltuieli setata */
            with recs as (
                select
                    a.coarea,
                    a.hier,
                    a.data_set,
                    c.cost_driver,
                    a.opex_categ,
                    c.nume as opex_categ_nume,
                    d.blocat as opex_categ_blocat,
                    a.ic_part,
                    e.nume as ic_part_nume,
                    cast((case when a.coarea = e.coarea then 1 else 0 end) as bit) as ic_part_blocat,
                    b.an,
                    sum(b.valoare) as valoare
                from oxpl.tbl_int_recs_plan_head as a

                inner join oxpl.tbl_int_recs_plan_vals as b
                on a.id = b.head_id

                inner join oxpl.tbl_int_opex_categ as c
                on a.opex_categ = c.cod

                inner join oxpl.tbl_int_opex_categ_assign as d
                on a.coarea = d.coarea and a.opex_categ = d.opex_categ

                left join oxpl.tbl_int_ic_part as e
                on a.ic_part = e.cod

                where a.data_set = @main_set and a.hier = @hier and c.cost_driver = @cdriver
                group by a.coarea, a.hier, a.data_set, c.cost_driver, a.opex_categ, c.nume, d.blocat,
                    a.ic_part, e.nume, cast((case when a.coarea = e.coarea then 1 else 0 end) as bit), b.an

                union all

                select
                    a.coarea,
                    a.hier,
                    a.data_set,
                    c.cost_driver,
                    a.opex_categ,
                    c.nume as opex_categ_nume,
                    d.blocat as opex_categ_blocat,
                    a.ic_part,
                    e.nume as ic_part_nume,
                    cast((case when a.coarea = e.coarea then 1 else 0 end) as bit) as ic_part_blocat,
                    b.an,
                    sum(b.valoare) as valoare
                from oxpl.tbl_int_recs_plan_head as a

                inner join oxpl.tbl_int_recs_plan_vals as b
                on a.id = b.head_id

                inner join oxpl.tbl_int_opex_categ as c
                on a.opex_categ = c.cod

                inner join oxpl.tbl_int_opex_categ_assign as d
                on a.coarea = d.coarea and a.opex_categ = d.opex_categ

                left join oxpl.tbl_int_ic_part as e
                on a.ic_part = e.cod

                where a.data_set = @compar_set and a.hier = @hier and c.cost_driver = @cdriver
                group by a.coarea, a.hier, a.data_set, c.cost_driver, a.opex_categ, c.nume, d.blocat,
                    a.ic_part, e.nume, cast((case when a.coarea = e.coarea then 1 else 0 end) as bit), b.an
            )
            select
                a.*,
                cast((select
                        n.data_set,
                        n.an,
                        n.valoare
                    from recs as n
                    where n.opex_categ = a.opex_categ and
                        (n.ic_part = a.ic_part or (n.ic_part is null and a.ic_part is null))
            order by n.data_set asc, n.an asc
            for json path) as nvarchar(max)) as valori
            from (select distinct
                    m.coarea,
                    m.hier,
                    @main_set as data_set,
                    m.cost_driver,
                    m.opex_categ,
                    m.opex_categ_nume,
                    m.opex_categ_blocat,
                    m.ic_part,
                    m.ic_part_nume,
                    m.ic_part_blocat
                from recs as m) as a
            order by a.opex_categ asc, a.ic_part asc;
        
        else /* versiune cu categorie cheltuieli setata */
            begin
                /* creare lista comuna pentru grupuri de centre de cost */
                drop table if exists #joined_cgrup;
                select distinct
                    a.cod,
                    first_value(a.nume) over (partition by a.cod order by (case a.data_set when @main_set then 0 else 1 end) asc
                                            rows between unbounded preceding and unbounded following) as nume,
                    first_value(b.cod) over (partition by a.cod order by (case a.data_set when @main_set then 0 else 1 end) asc
                                            rows between unbounded preceding and unbounded following) as superior
                into #joined_cgrup
                from oxpl.tbl_int_ccntr_grup as a

                left join oxpl.tbl_int_ccntr_grup as b
                on a.superior = b.id

                where a.data_set in (@main_set, @compar_set) and a.hier = @hier;

                /* creare lista comuna pentru centre de cost */
                drop table if exists #joined_ccenter;
                select distinct
                    a.cod,
                    first_value(a.nume) over (partition by a.cod order by (case a.data_set when @main_set then 0 else 1 end) asc
                                            rows between unbounded preceding and unbounded following) as nume,
                    first_value(a.grup) over (partition by a.cod order by (case a.data_set when @main_set then 0 else 1 end) asc
                                            rows between unbounded preceding and unbounded following) as superior,
                    (case first_value(a.data_set) over (partition by a.cod order by (case a.data_set when @main_set then 0 else 1 end) asc
                                            rows between unbounded preceding and unbounded following)
                        when @main_set then a.blocat else cast(1 as bit) end) as blocat
                into #joined_ccenter
                from oxpl.tbl_int_ccntr as a
                where a.data_set in (@main_set, @compar_set) and a.hier = @hier;

                /* creare rezultat inregistrari */
                with recs as (
                    select
                        a.data_set,
                        a.cost_centre,
                        a.ic_part,
                        c.nume as ic_part_nume,
                        cast((case when a.coarea = c.coarea then 1 else 0 end) as bit) as ic_part_blocat,
                        b.an,
                        sum(b.valoare) as valoare
                    from oxpl.tbl_int_recs_plan_head as a

                    inner join oxpl.tbl_int_recs_plan_vals as b
                    on a.id = b.head_id

                    left join oxpl.tbl_int_ic_part as c
                    on a.ic_part = c.cod

                    where a.data_set in (@main_set, @compar_set) and a.hier = @hier and a.opex_categ = @ocateg
                    group by a.data_set, a.cost_centre, a.ic_part, c.nume, cast((case when a.coarea = c.coarea then 1 else 0 end) as bit), b.an
                ),
                cte as (
                    select
                        a.cod,
                        a.nume,
                        a.superior,
                        cast(0 as tinyint) as nivel
                    from #joined_cgrup as a
                    where a.superior is null

                    union all

                    select
                        a.cod,
                        a.nume,
                        a.superior,
                        cast((b.nivel + 1) as tinyint) as nivel
                    from #joined_cgrup as a
                    inner join cte as b
                    on a.superior = b.cod
                )
                select * from
                    (select
                        @hier as hier,
                        @main_set as data_set,
                        a.cod as cost_centre,
                        a.nume as cost_centre_nume,
                        a.superior as cost_centre_super,
                        cast(0 as bit) as cost_centre_blocat,
                        cast(0 as bit) as cost_centre_leaf,
                        a.nivel as cost_centre_nivel,
                        @cdriver as cost_driver,
                        @ocateg as opex_categ,
                        cast(null as varchar(5)) as ic_part,
                        cast(null as nvarchar(50)) as ic_part_nume,
                        cast(null as bit) as ic_part_blocat,
                        cast(null as nvarchar(max)) as valori 
                    from cte as a

                    union all

                    select
                        @hier as hier,
                        @main_set as data_set,
                        a.cod as cost_centre,
                        a.nume as cost_centre_nume,
                        a.superior as cost_centre_super,
                        a.blocat as cost_centre_blocat,
                        cast(1 as bit) as cost_centre_leaf,
                        cast((b.nivel + 1) as tinyint) as cost_centre_nivel,
                        @cdriver as cost_driver,
                        @ocateg as opex_categ,
                        c.ic_part,
                        c.ic_part_nume,
                        c.ic_part_blocat,
                        cast((select
                                m.data_set,
                                m.an,
                                m.valoare
                            from recs as m
                            where m.cost_centre = a.cod and
                            (m.ic_part = c.ic_part or (m.ic_part is null and c.ic_part is null))
                            order by m.data_set asc, m.an asc
                            for json path) as nvarchar(max)) as valori
                    from #joined_ccenter as a

                    inner join cte as b
                    on a.superior = b.cod

                    left join (select distinct
                                    m.cost_centre,
                                    m.ic_part,
                                    m.ic_part_nume,
                                    m.ic_part_blocat
                                from recs as m) as c
                    on a.cod = c.cost_centre) as rsl
                order by cost_centre_nivel asc, cost_centre asc, ic_part asc;
            end;
    end;