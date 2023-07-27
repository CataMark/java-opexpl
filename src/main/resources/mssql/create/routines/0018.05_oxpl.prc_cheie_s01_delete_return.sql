create or alter procedure oxpl.prc_cheie_s01_delete_return
    @id int,
    @kid varchar(20)
as
    begin
        set nocount on;

        exec sys.sp_set_session_context @key = N'oxpl_user_id', @value = @kid, @readonly = 0;

        begin try
            delete a from oxpl.tbl_int_key_vals as a where a.cheie = @id;
            delete a from oxpl.tbl_int_key_head as a where a.id = @id;

            /* testare operatiune reusita */
            if exists (select * from oxpl.tbl_int_key_head as a where a.id = @id)
                select cast(0 as bit) as rezultat;
            else
                select cast(1 as bit) as rezultat;
        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            throw;
        end catch
    end;