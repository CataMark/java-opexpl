create or alter function oxpl.fnc_icpart_get_by_cod(
    @cod varchar(5)
)
returns table
as
return
    select top 1
        a.*,
        b.nume as coarea_nume
    from oxpl.tbl_int_ic_part as a
    left join oxpl.tbl_int_coarea as b
    on a.coarea = b.cod
    where a.cod = @cod;