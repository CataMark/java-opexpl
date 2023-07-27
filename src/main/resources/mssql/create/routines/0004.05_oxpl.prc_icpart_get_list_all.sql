create or alter procedure oxpl.prc_icpart_get_list_all
as
    select
        a.*,
        b.nume as coarea_nume
    from oxpl.tbl_int_ic_part as a
    left join oxpl.tbl_int_coarea as b
    on a.coarea = b.cod
    order by a.cod asc;