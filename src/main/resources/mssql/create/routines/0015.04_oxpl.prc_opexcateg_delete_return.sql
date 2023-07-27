create or alter procedure oxpl.prc_opexcateg_delete_return
    @cod int
as
    begin
        set nocount on;

        delete from oxpl.tbl_int_opex_categ where cod = @cod;

        /* testare operatiune reusita */
        if exists (select * from oxpl.tbl_int_opex_categ where cod = @cod)
            select cast(0 as bit) as rezultat;
        else
            select cast(1 as bit) as rezultat;
    end;