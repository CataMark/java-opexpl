create or alter function oxpl.fnc_costdriver_get_by_cod(
    @cod char(5)
)
returns table
as
return
    select top 1 * from oxpl.tbl_int_cost_driver where cod = @cod;