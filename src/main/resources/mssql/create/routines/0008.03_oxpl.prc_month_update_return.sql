create or alter procedure oxpl.prc_month_update_return
    @cod char(2),
    @nume nvarchar(50),
    @kid varchar(20)
as
    begin
        set nocount on;

        update oxpl.tbl_int_month
        set nume = @nume, mod_de = @kid, mod_timp = current_timestamp
        where cod = @cod;

        select * from oxpl.fnc_month_get_by_cod(@cod);
    end;