create or alter procedure oxpl.prc_planvers_update_return
    @cod char(3),
    @nume nvarchar(50),
    @kid varchar(20)
as
    begin
        set nocount on;

        update oxpl.tbl_int_plan_vers set
        nume = @nume, mod_de = @kid, mod_timp = current_timestamp
        where cod = @cod;

        select * from oxpl.fnc_planvers_get_by_cod(@cod);
    end;