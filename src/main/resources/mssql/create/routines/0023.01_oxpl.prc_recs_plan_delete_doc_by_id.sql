create or alter procedure oxpl.prc_recs_plan_delete_doc_by_id
    @id uniqueidentifier,
    @kid varchar(20)
as
    begin
        if session_context(N'oxpl_not_backup') is null
            if exists (select * from oxpl.tbl_int_recs_plan_head as a
                        inner join oxpl.tbl_int_recs_plan_vals as b
                        on a.id = b.head_id
                        inner join oxpl.tbl_int_data_set_per as c
                        on b.data_set = c.data_set and b.an = c.an and b.per = c.per

                        inner join (select data_set, coarea from oxpl.tbl_int_recs_plan_head where id = @id) as d
                        on a.data_set = d.data_set and a.coarea = d.coarea
                        where c.actual = 0)
                exec sys.sp_set_session_context @key = N'oxpl_not_backup', @value = 0, @readonly = 0;
            else
                exec sys.sp_set_session_context @key = N'oxpl_not_backup', @value = 1, @readonly = 0;

        if cast(session_context(N'oxpl_not_backup') as bit) = 0
            exec sys.sp_set_session_context @key = N'oxpl_user_id', @value = @kid, @readonly = 0;

        begin try
            begin transaction
                delete from oxpl.tbl_int_recs_plan_vals where head_id = @id;
                delete from oxpl.tbl_int_recs_plan_head where id = @id;
            commit transaction
        end try
        begin catch
            if @@trancount > 0
                rollback transaction;
            throw;
        end catch
    end;