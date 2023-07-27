create or alter procedure oxpl.prc_dataset_get_list_plan_ani
as
    select distinct a.an
    from oxpl.tbl_int_data_set as a
    inner join oxpl.tbl_int_plan_vers as b
    on a.vers = b.cod
    where b.actual = 0
    order by a.an asc;