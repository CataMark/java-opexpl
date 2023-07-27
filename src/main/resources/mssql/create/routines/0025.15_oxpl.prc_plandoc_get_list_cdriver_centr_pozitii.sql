create or alter procedure oxpl.prc_plandoc_get_list_cdriver_centr_pozitii
    @set int,
    @hier char(5),
    @cdriver char(5),
    @ocateg int = null
as
    begin
        if @ocateg is null
            with recs as (
                select
                    a.coarea,
                    a.hier,
                    a.data_set,
                    a.cost_centre,
                    c.nume as cost_centre_nume,
                    c.blocat as cost_centre_blocat,
                    b.an,
                    b.per,
                    sum(b.valoare) as valoare
                from oxpl.tbl_int_recs_plan_head as a

                inner join oxpl.tbl_int_recs_plan_vals as b
                on a.id = b.head_id

                inner join oxpl.tbl_int_ccntr as c
                on a.data_set = c.data_set and a.hier = c.hier and a.cost_centre = c.cod

                inner join oxpl.tbl_int_opex_categ as d
                on a.opex_categ = d.cod

                where a.data_set = @set and a.hier = @hier and d.cost_driver = @cdriver
                group by a.coarea, a.hier, a.data_set, a.cost_centre, c.nume, c.blocat, b.an, b.per
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

        else
            select
                a.id,
                a.coarea,
                (case a.descr when '(actual)' then null else a.descr end) as descr,
                a.hier,
                a.data_set,
                a.cost_centre,
                b.nume as cost_centre_nume,
                b.blocat as cost_centre_blocat,
                a.cheie,
                c.nume as cheie_nume,
                c.blocat as cheie_blocat,
                a.ic_part,
                d.nume as ic_part_nume,
                cast((case a.coarea when d.coarea then 1 else 0 end) as bit) as ic_part_blocat,
                a.mod_de,
                a.mod_timp,
                cast((select distinct
                    first_value(case m.cont when e.cont_ccoa then m.id else null end) over (partition by m.an, m.per order by (case m.cont when e.cont_ccoa then m.id else null end) desc
                        rows between unbounded preceding and unbounded following) as id,
                    m.head_id,
                    e.cont_ccoa as cont,
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

            left join oxpl.tbl_int_ic_part as d
            on a.ic_part = d.cod

            inner join oxpl.tbl_int_opex_categ as e
            on a.opex_categ = e.cod

            where a.data_set = @set and a.hier = @hier and a.opex_categ = @ocateg
            order by b.grup asc, a.cost_centre asc, a.ic_part asc, a.cheie asc, a.id asc;
    end;