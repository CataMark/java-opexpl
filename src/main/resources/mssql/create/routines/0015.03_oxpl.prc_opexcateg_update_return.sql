create or alter procedure oxpl.prc_opexcateg_update_return
    @cod int,
    @nume nvarchar(50),
    @cont_ccoa char(10),
    @kid varchar(10)
as
    begin
        set nocount on;

        update oxpl.tbl_int_opex_categ
        set nume = @nume, cont_ccoa = @cont_ccoa, mod_de = @kid, mod_timp = current_timestamp
        where cod = @cod;

        select * from oxpl.fnc_opexcateg_get_by_cod(@cod);
    end;