create or alter procedure oxpl.prc_costcenter_delete_all
    @set int,
    @hier char(5),
    @kid varchar(20)
as
    begin
        set nocount on;
        begin try
        
            exec sys.sp_set_session_context @key = N'oxpl_user_id', @value = @kid, @readonly = 0;
            begin transaction
                delete from oxpl.tbl_int_ccntr where data_set = @set and hier = @hier;
            commit transaction

            /* testare operatiunea a reusit */
            if exists (select * from oxpl.tbl_int_ccntr where data_set = @set and hier = @hier)
                select cast(0 as bit) as rezultat;
            else
                select cast(1 as bit) as rezultat;
        end try 
        begin catch
            if @@trancount > 0 rollback transaction;
            throw;
        end catch
    end;