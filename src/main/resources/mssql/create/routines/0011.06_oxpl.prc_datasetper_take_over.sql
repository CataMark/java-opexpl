create or alter procedure oxpl.prc_datasetper_take_over
    @dest_set int,
    @from_set int,
    @kid varchar(20)
as
    begin
        begin try
            if exists (select * from oxpl.tbl_int_data_set_per where data_set = @dest_set)
                raiserror('Setul de date destinaţie deja conţine perioade!', 16, 1);
            
            begin transaction
                insert into oxpl.tbl_int_data_set_per (data_set, an, per, actual, mod_de, mod_timp)
                select
                    @dest_set,
                    a.an,
                    a.per,
                    a.actual,
                    @kid,
                    current_timestamp
                from oxpl.tbl_int_data_set_per as a
                where a.data_set = @from_set;
            commit transaction
        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            throw;
        end catch
    end;