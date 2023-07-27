create or alter procedure oxpl.prc_icpart_delete_return
    @cod varchar(5)
as
    begin
        set nocount on;

        delete from oxpl.tbl_int_ic_part where cod = @cod;

        /* testare operatiune reusita */
        if exists (select * from oxpl.tbl_int_ic_part where cod = @cod)
            select cast(0 as bit) as rezultat;
        else
            select cast(1 as bit) as rezultat;
    end;