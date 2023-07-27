create or alter procedure oxpl.prc_plandoc_get_raw_ccenter_ncentr_pozitii
    @set int,
    @hier char(5),
    @cost_centre varchar(10),
    @cdriver_central bit = null,
    @kid varchar(20)
as
    select
        a.id as doc_id,
        a.descr,
        a.coarea,
        a.data_set,
        a.cost_centre,
        c.nume as cost_centre_nume,
        e.cost_driver,
        f.nume as cost_driver_nume,
        f.central as cost_driver_central,
        a.opex_categ,
        e.nume as opex_categ_nume,
        a.ic_part,
        g.nume as ic_part_nume,
        a.cheie,
        d.nume as cheie_nume,
        b.id as val_id,
        b.an,
        b.per,
        h.actual,
        b.valoare,
        a.mod_de as doc_modif_de,
        a.mod_timp as doc_modif_la,
        b.mod_de as val_modif_de,
        b.mod_timp as val_modif_la
    from oxpl.tbl_int_recs_plan_head as a

    inner join oxpl.tbl_int_recs_plan_vals as b
    on a.id = b.head_id

    inner join oxpl.fnc_hier_get_childs(@hier, @set, @cost_centre, @kid) as c
    on a.cost_centre = c.cod

    left join oxpl.tbl_int_key_head as d
    on a.cheie = d.id

    inner join oxpl.tbl_int_opex_categ as e
    on a.opex_categ = e.cod

    inner join oxpl.tbl_int_cost_driver as f
    on e.cost_driver = f.cod

    left join oxpl.tbl_int_ic_part as g
    on a.ic_part = g.cod

    inner join oxpl.tbl_int_data_set_per as h
    on b.data_set = h.data_set and b.an = h.an and b.per = h.per

    where a.data_set = @set and a.hier = @hier and
        1 = (case when @cdriver_central is null then 1 else iif(f.central = @cdriver_central, 1, 0) end)
    order by a.cost_centre asc, e.cost_driver asc, a.opex_categ asc, a.ic_part asc, a.cheie asc, a.id asc,
        b.an asc, b.per asc, b.cont asc, b.id asc;