create or alter procedure oxpl.prc_plandoc_get_list_ccenter_ncentr_sumar
    @set int,
    @hier char(5),
    @cost_centre varchar(10),
    @cdriver_central bit = null,
    @kid varchar(20)
as
    with recs as (
        select
            a.coarea,
            a.hier,
            a.data_set,
            @cost_centre as cost_centre,
            d.cost_driver,
            e.nume as cost_driver_nume,
            e.central as cost_driver_central,
            a.opex_categ,
            d.nume as opex_categ_nume,
            g.blocat as opex_categ_blocat,
            a.ic_part,
            f.nume as ic_part_nume,
            cast((case when a.coarea = f.coarea then 1 else 0 end) as bit) as ic_part_blocat,
            b.an,
            b.per,
            sum(b.valoare) as valoare
        from oxpl.tbl_int_recs_plan_head as a

        inner join oxpl.tbl_int_recs_plan_vals as b
        on a.id = b.head_id

        inner join oxpl.fnc_hier_get_childs(@hier, @set, @cost_centre, @kid) as c
        on a.cost_centre = c.cod

        inner join oxpl.tbl_int_opex_categ as d
        on a.opex_categ = d.cod

        inner join oxpl.tbl_int_cost_driver as e
        on d.cost_driver = e.cod

        left join oxpl.tbl_int_ic_part as f
        on a.ic_part = f.cod

        inner join oxpl.tbl_int_opex_categ_assign as g
        on a.opex_categ = g.opex_categ and a.coarea = g.coarea

        where a.data_set = @set and a.hier = @hier and 
            1 = (case when @cdriver_central is null then 1 else iif(e.central = @cdriver_central, 1, 0) end)
        group by a.coarea, a.hier, a.data_set, d.cost_driver, e.nume, e.central, a.opex_categ, d.nume, g.blocat, a.ic_part, f.nume,
                cast((case when a.coarea = f.coarea then 1 else 0 end) as bit), b.an, b.per
    )
    select
        a.*,
        cast((select
                n.an,
                n.per,
                n.valoare
            from recs as n
            where n.cost_driver = a.cost_driver and n.opex_categ = a.opex_categ and
                (n.ic_part = a.ic_part or (n.ic_part is null and a.ic_part is null))
            order by n.an asc, n.per asc
            for json path) as nvarchar(max)) as valori
    from (select distinct
            m.coarea,
            m.hier,
            m.data_set,
            m.cost_centre,
            m.cost_driver,
            m.cost_driver_nume,
            m.cost_driver_central,
            m.opex_categ,
            m.opex_categ_nume,
            m.opex_categ_blocat,
            m.ic_part,
            m.ic_part_nume,
            m.ic_part_blocat
        from recs as m) as a
    order by a.cost_driver_central asc, a.cost_driver asc, a.opex_categ asc, a.ic_part asc;