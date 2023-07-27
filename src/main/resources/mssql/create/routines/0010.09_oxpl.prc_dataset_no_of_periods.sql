create or alter procedure oxpl.prc_dataset_no_of_periods
    @id int
as
    select count(*) as numar from oxpl.tbl_int_data_set_per where data_set = @id;