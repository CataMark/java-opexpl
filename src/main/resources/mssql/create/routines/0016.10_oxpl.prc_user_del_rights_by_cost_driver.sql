create or alter procedure oxpl.prc_user_del_rights_by_cost_driver
    @uname varchar(20),
    @coarea char(4),
    @cost_driver char(5)
as
    begin
        set nocount on;

        delete from oxpl.tbl_int_users_cost_drivers
        where uname = @uname and coarea = @coarea and cost_driver = @cost_driver;

        /* testare operatiune reusita */
        if exists (select * from oxpl.tbl_int_users_cost_drivers where uname = @uname and coarea = @coarea and cost_driver = @cost_driver)
            select cast(0 as bit) as rezultat;
        else
            select cast(1 as bit) as rezultat;
    end;