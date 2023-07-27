create or alter procedure oxpl.prc_datasetper_delete_return
    @id uniqueidentifier
as
    begin
        set nocount on;

        delete from oxpl.tbl_int_data_set_per where id = @id;

        /* testare operatiunea a reusit */
        if exists (select * from oxpl.tbl_int_data_set_per where id = @id)
            select cast(0 as bit) as rezultat;
        else
            select cast(1 as bit) as rezultat;
    end;