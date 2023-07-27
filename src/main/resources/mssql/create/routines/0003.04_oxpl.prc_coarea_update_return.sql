create or alter procedure oxpl.prc_coarea_update_return
    @cod char(4),
    @nume nvarchar(100),
    @acronim varchar(5),
    @alocare bit,
    @hier char(5),
    @kid varchar(20)
as
    begin
        set nocount on;

        update oxpl.tbl_int_coarea
        set nume = @nume, acronim = @acronim, alocare = @alocare, cc_hier = @hier, mod_de = @kid, mod_timp = current_timestamp 
        where cod = @cod;

        select * from oxpl.fnc_coarea_get_by_cod(@cod);
    end;