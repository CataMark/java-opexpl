create or alter procedure oxpl.prc_opexcateg_asign_get_list_not_asign
    @coarea char(4),
    @cost_driver char(5)
as
    select
        a.cod,
        a.nume,
        a.cost_driver,
        b.nume as cost_driver_nume,
        a.cont_ccoa,
        cast(0 as bit) as blocat,
        a.mod_de,
        a.mod_timp
    from oxpl.tbl_int_opex_categ as a

    inner join oxpl.tbl_int_cost_driver as b
    on a.cost_driver = b.cod

    left join
        (select a2.cod
        from oxpl.tbl_int_opex_categ_assign as a1
        inner join oxpl.tbl_int_opex_categ as a2
        on a1.opex_categ = a2.cod
        where a1.coarea = @coarea and a2.cost_driver = @cost_driver) as c
    on a.cod = c.cod
    where a.cost_driver = @cost_driver and c.cod is null
    order by a.nume asc;