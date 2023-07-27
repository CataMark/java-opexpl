create or alter procedure oxpl.prc_plandoc_get_list_ccenter_ncentr_pozitii
    @set int,
    @hier char(5),
    @cost_centre varchar(10),
    @cdriver_central bit = null,
    @kid varchar(20)
as
    begin
        declare @isgroup bit = 1;
        if exists (select * from oxpl.tbl_int_ccntr as a where a.data_set = @set and a.hier = @hier and a.cod = @cost_centre)
            set @isgroup = 0;

        if @isgroup = 0 
            select
                a.id,
                a.coarea,
                (case a.descr when '(actual)' then null else a.descr end) as descr,
                a.hier,
                a.data_set,
                a.cost_centre,
                b.nume as cost_centre_nume,
                a.cheie,
                c.nume as cheie_nume,
                c.blocat as cheie_blocat,
                d.cost_driver,
                e.nume as cost_driver_nume,
                e.central as cost_driver_central,
                a.opex_categ,
                d.nume as opex_categ_nume,
                f.blocat as opex_categ_blocat,
                a.ic_part,
                g.nume as ic_part_nume,
                cast((case a.coarea when g.coarea then 1 else 0 end) as bit) as ic_part_blocat,
                a.mod_de,
                a.mod_timp,
                cast((select distinct
                    first_value(case m.cont when d.cont_ccoa then m.id else null end) over (partition by m.an, m.per order by (case m.cont when d.cont_ccoa then m.id else null end) desc
                        rows between unbounded preceding and unbounded following) as id,
                    m.head_id,
                    d.cont_ccoa as cont,
                    m.data_set,
                    m.an,
                    m.per,
                    n.actual,
                    sum(m.valoare) over (partition by m.an, m.per) as valoare,
                    first_value(m.mod_de) over (partition by m.head_id order by m.mod_timp desc rows between unbounded preceding and unbounded following) as mod_de,
                    max(m.mod_timp) over (partition by m.head_id) as mod_timp
                from oxpl.tbl_int_recs_plan_vals as m
                inner join oxpl.tbl_int_data_set_per as n
                on m.data_set = n.data_set and m.an = n.an and m.per = n.per
                where m.head_id = a.id
                for json path) as nvarchar(max)) as valori
            from oxpl.tbl_int_recs_plan_head as a

            inner join oxpl.tbl_int_ccntr as b
            on a.data_set = b.data_set and a.hier = b.hier and a.cost_centre = b.cod

            left join oxpl.tbl_int_key_head as c
            on a.cheie = c.id

            inner join oxpl.tbl_int_opex_categ as d
            on a.opex_categ = d.cod

            inner join oxpl.tbl_int_cost_driver as e
            on d.cost_driver = e.cod

            inner join oxpl.tbl_int_opex_categ_assign as f
            on a.coarea = f.coarea and a.opex_categ = f.opex_categ

            left join oxpl.tbl_int_ic_part as g
            on a.ic_part = g.cod

            where a.data_set = @set and a.hier = @hier and a.cost_centre = @cost_centre and
                1 = (case when @cdriver_central is null then 1 else iif(e.central = @cdriver_central, 1, 0) end)
            order by e.central asc, d.cost_driver asc, a.opex_categ asc, a.ic_part asc, a.cheie asc, a.id asc;

        else
            with recs as(
                select
                    a.coarea,
                    a.hier,
                    a.data_set,
                    a.cost_centre,
                    d.nume as cost_centre_nume,
                    d.blocat as cost_centre_blocat,
                    b.an,
                    b.per,
                    sum(b.valoare) as valoare
                from oxpl.tbl_int_recs_plan_head as a

                inner join oxpl.tbl_int_recs_plan_vals as b
                on a.id = b.head_id

                inner join oxpl.fnc_hier_get_childs(@hier, @set, @cost_centre, @kid) as c
                on a.cost_centre = c.cod

                inner join oxpl.tbl_int_ccntr as d
                on a.hier = d.hier and a.data_set = d.data_set and a.cost_centre = d.cod

                inner join oxpl.tbl_int_opex_categ as e
                on a.opex_categ = e.cod

                inner join oxpl.tbl_int_cost_driver as f
                on e.cost_driver = f.cod

                where a.data_set = @set and a.hier = @hier and
                    1 = (case when @cdriver_central is null then 1 else iif(f.central = @cdriver_central, 1, 0) end)
                group by a.coarea, a.hier, a.data_set, a.cost_centre, d.nume, d.blocat, b.an, b.per
            )
            select
                a.*,
                cast((select
                        n.an,
                        n.per,
                        n.valoare
                    from recs as n
                    where n.cost_centre = a.cost_centre
                    order by n.an asc, n.per asc
                    for json path) as nvarchar(max)) as valori
            from (select distinct
                    m.coarea,
                    m.hier,
                    m.data_set,
                    m.cost_centre,
                    m.cost_centre_nume,
                    m.cost_centre_blocat
                from recs as m) as a
            order by a.cost_centre asc;
    end;