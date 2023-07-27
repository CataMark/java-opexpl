create or alter procedure oxpl.prc_costdriver_asign_get_list_all
    @coarea char(4)
as
    select b.id, a.cod, a.nume, a.central, b.blocat, b.mod_de, b.mod_timp
    from oxpl.tbl_int_cost_driver as a
    inner join oxpl.tbl_int_cost_driver_assign as b
    on a.cod = b.cost_driver
    where b.coarea = @coarea
    order by a.cod asc;