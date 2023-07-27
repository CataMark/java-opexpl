create or alter procedure oxpl.prc_bussasg_get_list_asign_to_coarea
    @coarea char(4)
as
    select
        a.*,
        b.nume as coarea_nume,
        c.seg_ind as buss_line_seg,
        c.nume as buss_line_nume
    from oxpl.tbl_int_buss_line_asg a

    inner join oxpl.tbl_int_coarea b
    on a.coarea = b.cod

    inner join oxpl.tbl_int_buss_line c
    on a.buss_line  = c.cod

    where a.coarea = @coarea
    order by a.coarea asc, a.buss_line asc;