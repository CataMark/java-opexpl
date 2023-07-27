create or alter procedure oxpl.prc_opexcateg_get_list_all
as
    select
        a.*,
        b.nume as cost_driver_nume
    from oxpl.tbl_int_opex_categ as a
    inner join oxpl.tbl_int_cost_driver as b
    on a.cost_driver = b.cod
    order by a.cost_driver asc, a.nume asc;