create or alter function oxpl.fnc_opexcateg_get_by_cod(
    @cod int
)
returns table
as
return
    select top 1
        a.*,
        b.nume as cost_driver_nume
    from oxpl.tbl_int_opex_categ as a
    inner join oxpl.tbl_int_cost_driver as b
    on a.cost_driver = b.cod
    where a.cod = @cod;