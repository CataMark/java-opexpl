create or alter function oxpl.fnc_dataset_get_by_id(
    @id int
)
returns table
as
return
    select top 1
        a.*,
        c.actual,
        cast(b.nume as nvarchar(30)) as impl_compare_nume,
        cast(b.an as smallint) as impl_compare_an,
        cast(b.vers as char(3)) as impl_compare_vers,
        cast(d.nume as nvarchar(30)) as actual_set_nume,
        cast(d.an as smallint) as actual_set_an,
        cast(d.vers as char(3)) as actual_set_vers
    from oxpl.tbl_int_data_set as a

    left join oxpl.tbl_int_data_set as b
    on a.impl_compare = b.id

    inner join oxpl.tbl_int_plan_vers as c
    on a.vers = c.cod

    left join oxpl.tbl_int_data_set as d
    on a.actual_set = d.id

    where a.id = @id;