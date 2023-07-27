create or alter function oxpl.fnc_datasetper_get_by_id(
    @id uniqueidentifier
)
returns table
as
return
    select top 1 a.*, b.nume as per_nume
    from oxpl.tbl_int_data_set_per as a
    inner join oxpl.tbl_int_month as b
    on a.per = b.cod
    where a.id = @id;