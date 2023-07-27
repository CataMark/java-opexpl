create or alter procedure oxpl.prc_costcentergroup_delete_return
    @id uniqueidentifier,
    @kid varchar(20)
as
    begin
        set nocount on;

        exec sys.sp_set_session_context @key = N'oxpl_user_id', @value = @kid, @readonly = 0;
        delete from oxpl.tbl_int_ccntr_grup where id = @id;

        /* testare operatiunea a reusit */
        if exists (select * from oxpl.tbl_int_ccntr_grup where id = @id)
            select cast(0 as bit) as rezultat;
        else
            select cast(1 as bit) as rezultat;
    end;