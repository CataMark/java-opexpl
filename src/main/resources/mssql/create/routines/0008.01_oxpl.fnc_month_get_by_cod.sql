create or alter function oxpl.fnc_month_get_by_cod(
    @cod char(2)
)
returns table
as
return
    select top 1 * from oxpl.tbl_int_month where cod = @cod;