create or alter procedure oxpl.prc_costdriver_update_return
    @cod char(5),
    @nume nvarchar(50),
    @central bit,
    @kid varchar(20)
as
    begin
        set nocount on;

        update oxpl.tbl_int_cost_driver
        set nume = @nume, central = @central, mod_de = @kid, mod_timp = current_timestamp
        where cod = @cod;

        select * from oxpl.fnc_costdriver_get_by_cod(@cod);
    end;