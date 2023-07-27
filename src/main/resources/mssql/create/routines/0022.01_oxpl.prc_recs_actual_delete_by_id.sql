create or alter procedure oxpl.prc_recs_actual_delete_by_id
    @id uniqueidentifier,
    @kid varchar(20)
as
    begin
        exec sys.sp_set_session_context @key = N'oxpl_user_id', @value = @kid, @readonly = 0;
        delete from oxpl.tbl_int_recs_act where id = @id;
    end;