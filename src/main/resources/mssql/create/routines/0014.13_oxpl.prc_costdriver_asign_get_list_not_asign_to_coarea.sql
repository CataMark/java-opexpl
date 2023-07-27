create or alter procedure oxpl.prc_costdriver_asign_get_list_not_asign_to_coarea
    @coarea char(4)
as
    select a.cod, a.nume, a.central, cast(0 as bit) as blocat, b.mod_de, b.mod_timp
    from oxpl.tbl_int_cost_driver as a
    left join (select * from oxpl.tbl_int_cost_driver_assign where coarea = @coarea) as b
    on a.cod = b.cost_driver
    where b.cost_driver is null
    order by a.cod asc;