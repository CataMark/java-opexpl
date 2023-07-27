create or alter procedure oxpl.prc_dataset_delete_return
    @id int
as
    begin
        set nocount on;

        delete from oxpl.tbl_int_data_set where id = @id;

        /* testare operatiune reusita */
        if exists (select * from oxpl.tbl_int_data_set where id = @id)
            select cast(0 as bit) as rezultat;
        else
            select cast(1 as bit) as rezultat;
    end;