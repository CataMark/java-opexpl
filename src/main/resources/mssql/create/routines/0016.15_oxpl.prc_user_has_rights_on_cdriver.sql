create or alter procedure oxpl.prc_user_has_rights_on_cdriver
    @coarea char(4),
    @cdriver char(5),
    @kid varchar(20)
as
    if exists (select * from oxpl.tbl_int_cost_driver_assign as a
                inner join oxpl.tbl_int_users_cost_drivers as b
                on a.coarea = b.coarea and a.cost_driver = b.cost_driver
                where a.coarea = @coarea and a.cost_driver = @cdriver and b.uname = @kid)
        select cast(1 as bit) as rezultat;
    else
        select cast(0 as bit) as rezultat;