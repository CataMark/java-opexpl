create or alter function oxpl.fnc_coarea_get_by_hier(
    @hier char(5)
)
returns table
as
return
    select top 1 * from oxpl.tbl_int_coarea where cc_hier = @hier;