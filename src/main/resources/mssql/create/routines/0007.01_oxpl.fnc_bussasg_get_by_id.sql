create or alter function oxpl.fnc_bussasg_get_by_id(
    @id uniqueidentifier
)
returns table
as
return
    select top 1
        a.*,
        b.nume as coarea_nume,
        c.seg_ind as buss_line_seg,
        c.nume as buss_line_nume
    from oxpl.tbl_int_buss_line_asg a

    inner join oxpl.tbl_int_coarea b
    on a.coarea = b.cod

    inner join oxpl.tbl_int_buss_line c
    on a.buss_line  = c.cod

    where a.id = @id;