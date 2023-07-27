create or alter procedure oxpl.prc_dataset_get_list_raportare_ani
as
    select distinct a.an
    from oxpl.tbl_int_data_set as a
    where a.raportare = 1
    order by a.an asc;