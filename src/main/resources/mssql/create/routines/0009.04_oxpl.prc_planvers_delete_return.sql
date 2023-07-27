create or alter procedure oxpl.prc_planvers_delete_return
    @cod char(3)
as
    begin
        set nocount on;

        delete from oxpl.tbl_int_plan_vers where cod = @cod;

        /* testare operatiune reusita */
        if exists (select * from oxpl.tbl_int_plan_vers where cod = @cod)
            select cast(0 as bit) as rezultat;
        else
            select cast(1 as bit) as rezultat;
    end;