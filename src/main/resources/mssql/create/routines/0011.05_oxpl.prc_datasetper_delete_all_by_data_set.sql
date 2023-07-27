create or alter procedure oxpl.prc_datasetper_delete_all_by_data_set
    @set int
as
    begin
        begin try
            begin transaction
                delete from oxpl.tbl_int_data_set_per where data_set = @set;
            commit transaction
        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            throw;
        end catch
    end;