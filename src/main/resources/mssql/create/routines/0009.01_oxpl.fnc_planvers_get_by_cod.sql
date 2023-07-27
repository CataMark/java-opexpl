create or alter function oxpl.fnc_planvers_get_by_cod(
    @cod char(3)
)
returns table
as
return
    select top 1 * from oxpl.tbl_int_plan_vers where cod = @cod;