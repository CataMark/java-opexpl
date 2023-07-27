create or alter procedure oxpl.prc_planvers_insert_return
    @cod char(3),
    @nume nvarchar(50),
    @kid varchar(20)
as
    begin
        set nocount on;

        insert into oxpl.tbl_int_plan_vers (cod, nume, mod_de, mod_timp)
        values (@cod, @nume, @kid, current_timestamp);

        select * from oxpl.fnc_planvers_get_by_cod(@cod);
    end;