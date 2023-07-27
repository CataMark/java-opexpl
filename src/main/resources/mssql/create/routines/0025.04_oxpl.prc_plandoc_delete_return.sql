create or alter procedure oxpl.prc_plandoc_delete_return
    @id uniqueidentifier,
    @kid varchar(20)
as
    begin
        set nocount on;
        exec sys.sp_set_session_context @key = N'oxpl_user_id', @value = @kid, @readonly = 0;

        begin try
            if exists (select * from oxpl.tbl_int_recs_plan_head as a
                        inner join oxpl.tbl_int_recs_plan_vals as b
                        on a.id = b.head_id
                        inner join oxpl.tbl_int_data_set_per as c
                        on b.data_set = c.data_set and b.an = c.an and b.per = c.per
                        where a.id = @id and c.actual = 1)
                raiserror('Documentul conține valori pentru perioade realizate și nu poate fi șters!', 16, 1);

            begin transaction
                delete from oxpl.tbl_int_recs_plan_vals where head_id = @id;
                delete from oxpl.tbl_int_recs_plan_head where id = @id;
            commit transaction

            if exists (select * from oxpl.tbl_int_recs_plan_head where id = @id)
                select cast(0 as bit) as rezultat;
            else 
                select cast(1 as bit) as rezultat;
        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            throw;
        end catch
    end;