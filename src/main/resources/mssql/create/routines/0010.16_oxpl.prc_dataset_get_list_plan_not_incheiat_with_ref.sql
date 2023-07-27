create or alter procedure oxpl.prc_dataset_get_list_plan_not_incheiat_with_ref
as
    select
        a.*,
        b.actual
    from oxpl.tbl_int_data_set as a
    inner join oxpl.tbl_int_plan_vers as b
    on a.vers = b.cod
    where a.incheiat = 0 and (a.impl_compare is not null or a.actual_set is not null) and b.actual = 0;