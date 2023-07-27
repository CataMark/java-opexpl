create or alter procedure oxpl.prc_dataset_get_list_plan_by_an
    @an smallint
as
    select
        a.*,
        c.actual,
        b.nume as impl_compare_nume,
        b.an as impl_compare_an,
        b.vers as impl_compare_vers,
        d.nume as actual_set_nume,
        d.an as actual_set_an,
        d.vers as actual_set_vers
    from oxpl.tbl_int_data_set as a

    left join oxpl.tbl_int_data_set as b
    on a.impl_compare = b.id

    inner join oxpl.tbl_int_plan_vers as c
    on a.vers = c.cod

    left join oxpl.tbl_int_data_set as d
    on a.actual_set = d.id

    where a.an = @an and c.actual = 0
    order by a.an asc, a.vers asc, a.nume asc;