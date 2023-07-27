create or alter procedure oxpl.prc_dataset_get_list_ani
as
    select distinct an from oxpl.tbl_int_data_set order by an asc;