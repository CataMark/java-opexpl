create or alter procedure oxpl.prc_opexcateg_get_list_by_cost_driver
    @cost_driver char(5)
as
    select
        a.*,
        b.nume as cost_driver_nume
    from oxpl.tbl_int_opex_categ as a
    inner join oxpl.tbl_int_cost_driver as b
    on a.cost_driver = b.cod
    where a.cost_driver = @cost_driver
    order by a.nume asc;