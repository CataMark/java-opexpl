create or alter procedure oxpl.prc_opexcateg_asign_update_return
    @id uniqueidentifier,
    @blocat bit,
    @kid varchar(20)
as
    begin
        set nocount on;

        update oxpl.tbl_int_opex_categ_assign
        set blocat = @blocat, mod_de = @kid, mod_timp = current_timestamp
        where id = @id;

        select * from oxpl.fnc_opexcateg_asign_get_by_id(@id);
    end;