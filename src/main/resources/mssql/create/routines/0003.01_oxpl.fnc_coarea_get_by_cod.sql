create or alter function oxpl.fnc_coarea_get_by_cod(
    @cod char(4)
)
returns table
as
return
    select top 1 * from oxpl.tbl_int_coarea where cod = @cod;