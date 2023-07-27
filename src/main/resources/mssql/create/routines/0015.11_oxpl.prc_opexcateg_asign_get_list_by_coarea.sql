create or alter procedure oxpl.prc_opexcateg_asign_get_list_by_coarea
    @coarea char(4)
as
    select
        a.id,
        b.cod,
        b.nume,
        b.cost_driver,
        c.nume as cost_driver_nume,
        b.cont_ccoa,
        a.blocat,
        a.mod_de,
        a.mod_timp
    from oxpl.tbl_int_opex_categ_assign as a
    inner join oxpl.tbl_int_opex_categ as b
    on a.opex_categ = b.cod
    inner join oxpl.tbl_int_cost_driver as c
    on b.cost_driver = c.cod
    where a.coarea = @coarea
    order by b.cost_driver asc, b.nume asc;