create or alter procedure oxpl.prc_bussline_delete_return
    @cod char(4)
as
    begin
    set nocount on;

    delete from oxpl.tbl_int_buss_line where cod = @cod;

    /* testare operatiunea a reusit */
    if exists (select * from oxpl.tbl_int_buss_line where cod = @cod)
        select cast(0 as bit) as rezultat;
    else
        select cast(1 as bit) as rezultat;
end;