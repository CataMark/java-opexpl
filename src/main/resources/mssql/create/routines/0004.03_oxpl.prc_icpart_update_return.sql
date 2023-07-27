create or alter procedure oxpl.prc_icpart_update_return
    @cod varchar(5),
    @nume nvarchar(50),
    @coarea char(4),
    @kid varchar(20)
as
    begin
        set nocount on;

        update oxpl.tbl_int_ic_part
        set nume = @nume, coarea = @coarea, mod_de = @kid, mod_timp = current_timestamp
        where cod = @cod;

        select * from oxpl.fnc_icpart_get_by_cod(@cod);
    end;