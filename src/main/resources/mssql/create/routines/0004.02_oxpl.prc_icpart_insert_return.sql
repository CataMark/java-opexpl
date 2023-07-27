create or alter procedure oxpl.prc_icpart_insert_return
    @cod varchar(5),
    @nume nvarchar(50),
    @coarea char(4),
    @kid varchar(20)
as
    begin
    set nocount on;

    insert into oxpl.tbl_int_ic_part (cod, nume, coarea, mod_de, mod_timp)
    values (@cod, @nume, @coarea, @kid, current_timestamp);

    select * from oxpl.fnc_icpart_get_by_cod(@cod);
end;