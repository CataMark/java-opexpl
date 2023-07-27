create or alter procedure oxpl.prc_plandoc_get_raw_cdriver_centr_compar
    @main_set int,
    @compar_set int,
    @hier char(5),
    @cdriver char(5),
    @ocateg int = null
as
    select
        a.coarea,
        a.hier,
        a.data_set,
        a.cost_centre,
        c.nume as cost_centre_nume,
        c.blocat as cost_centre_blocat,
        d.cost_driver,
        e.nume as cost_driver_nume,
        a.opex_categ,
        d.nume as opex_categ_nume,
        g.blocat as opex_categ_blocat,
        a.ic_part,
        f.nume as ic_part_nume,
        cast((case when a.coarea = f.coarea then 1 else 0 end) as bit) as ic_part_blocat,
        b.an,
        b.per,
        h.actual,
        sum(b.valoare) as valoare
    from oxpl.tbl_int_recs_plan_head as a

    inner join oxpl.tbl_int_recs_plan_vals as b
    on a.id = b.head_id

    inner join oxpl.tbl_int_ccntr as c
    on a.data_set = c.data_set and a.hier = c.hier and a.cost_centre = c.cod

    inner join oxpl.tbl_int_opex_categ as d
    on a.opex_categ = d.cod

    inner join oxpl.tbl_int_cost_driver as e
    on d.cost_driver = e.cod

    left join oxpl.tbl_int_ic_part as f
    on a.ic_part = f.cod

    inner join oxpl.tbl_int_opex_categ_assign as g
    on a.opex_categ = g.opex_categ and a.coarea = g.coarea

    inner join oxpl.tbl_int_data_set_per as h
    on b.data_set = h.data_set and b.an = h.an and b.per = h.per

    where a.data_set in (@main_set, @compar_set) and a.hier = @hier and d.cost_driver = @cdriver and
        1 = (case when @ocateg is null then 1 else iif(a.opex_categ = @ocateg, 1, 0) end)
    group by a.coarea, a.hier, a.data_set, a.cost_centre, c.nume, c.blocat, d.cost_driver, e.nume, a.opex_categ, d.nume, g.blocat,
        a.ic_part, f.nume, cast((case when a.coarea = f.coarea then 1 else 0 end) as bit), b.an, b.per, h.actual;