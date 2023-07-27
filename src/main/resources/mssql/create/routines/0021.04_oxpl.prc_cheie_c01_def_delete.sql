create or alter procedure oxpl.prc_cheie_c01_def_delete
    @id int
as
    begin
        set nocount on;
        begin try
            begin transaction
                delete from oxpl.tbl_int_key_rule where cheie = @id;
                delete from oxpl.tbl_int_key_head where id = @id;
            commit transaction
        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            throw;
        end catch
    end;