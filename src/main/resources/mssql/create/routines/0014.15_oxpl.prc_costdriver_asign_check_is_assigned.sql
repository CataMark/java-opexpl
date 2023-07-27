create or alter procedure oxpl.prc_costdriver_asign_check_is_assigned
    @coarea char(4),
    @cdriver char(5)
as
    if exists (select * from oxpl.tbl_int_cost_driver_assign as a where a.coarea = @coarea and a.cost_driver = @cdriver)
        select cast(1 as bit) as rezultat;
    else
        select cast(0 as bit) as rezultat;