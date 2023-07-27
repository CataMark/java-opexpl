create or alter procedure oxpl.prc_costdriver_asign_delete_return
    @id uniqueidentifier
as
    begin
        set nocount on;

        delete from oxpl.tbl_int_cost_driver_assign where id = @id;

        /*  testare operatiunea a reusit */
        if exists (select * from oxpl.tbl_int_cost_driver_assign where id = @id)
            select cast(0 as bit) as rezultat;
        else
            select cast(1 as bit) as rezultat;
    end;