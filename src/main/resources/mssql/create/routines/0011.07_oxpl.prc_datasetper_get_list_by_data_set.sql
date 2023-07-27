create or alter procedure oxpl.prc_datasetper_get_list_by_data_set
    @set int
as
    select a.*, b.nume as per_nume
    from oxpl.tbl_int_data_set_per as a
    inner join oxpl.tbl_int_month as b
    on a.per = b.cod
    where a.data_set = @set
    order by a.an asc, a.per asc;