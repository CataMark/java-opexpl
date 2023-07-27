create or alter procedure oxpl.prc_icpart_get_list_not_refer_to_coarea
    @coarea char(4)
as
    select
        a.*,
        b.nume as coarea_nume
    from oxpl.tbl_int_ic_part as a
    left join oxpl.tbl_int_coarea as b
    on a.coarea = b.cod
    where a.coarea is null or a.coarea != @coarea
    order by a.cod asc;