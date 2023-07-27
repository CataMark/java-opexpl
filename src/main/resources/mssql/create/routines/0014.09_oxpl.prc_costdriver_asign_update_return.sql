create or alter procedure oxpl.prc_costdriver_asign_update_return
    @id uniqueidentifier,
    @blocat bit,
    @kid varchar(20)
as
    begin
        set nocount on;

        update oxpl.tbl_int_cost_driver_assign
        set blocat = @blocat, mod_de = @kid, mod_timp = current_timestamp
        where id = @id;

        select * from oxpl.fnc_costdriver_asign_get_by_id(@id);
    end;