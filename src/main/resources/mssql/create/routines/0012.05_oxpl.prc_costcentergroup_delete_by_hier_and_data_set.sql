create or alter procedure oxpl.prc_costcentergroup_delete_by_hier_and_data_set
    @hier char(5),
    @set int,
    @kid varchar(20)
as
    begin
        set nocount on;
        begin try
            exec sys.sp_set_session_context @key = N'oxpl_user_id', @value = @kid, @readonly = 0;

            begin transaction
                delete from oxpl.tbl_int_ccntr_grup where hier = @hier and data_set = @set;
            commit transaction

            /* testare operatiunea a reusit */
            if exists (select * from oxpl.tbl_int_ccntr_grup where hier = @hier and data_set = @set)
                select cast(0 as bit) as rezultat;
            else
                select cast(1 as bit) as rezultat;
        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            throw;
        end catch
    end;