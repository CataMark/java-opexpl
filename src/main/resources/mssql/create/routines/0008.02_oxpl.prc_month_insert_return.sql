create or alter procedure oxpl.prc_month_insert_return
    @cod char(2),
    @nume nvarchar(50),
    @kid varchar(20)
as
    begin
        set nocount on;

        insert into oxpl.tbl_int_month (cod, nume, mod_de, mod_timp)
        values (@cod, @nume, @kid, current_timestamp);

        select * from oxpl.fnc_month_get_by_cod(@cod);
    end;