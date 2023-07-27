create or alter procedure oxpl.prc_bussline_get_list_not_asign_to_coarea
    @coarea char(4)
as
    select a.*
    from oxpl.tbl_int_buss_line a
    left join
        (select distinct buss_line from oxpl.tbl_int_buss_line_asg where coarea = @coarea) b
    on a.cod = b.buss_line
    where b.buss_line is null
    order by a.cod asc;