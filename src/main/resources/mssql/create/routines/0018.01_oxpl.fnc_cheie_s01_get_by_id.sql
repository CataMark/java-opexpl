create or alter function oxpl.fnc_cheie_s01_get_by_id(
    @id int
)
returns table
as
return
    select top 1
        a.*,
        b.nume as cost_centre_nume,
        cast((select
            m.*,
            n.seg_ind as buss_line_seg,
            n.nume as buss_line_nume,
            b.nume as cost_centre_nume
        from oxpl.tbl_int_key_vals as m
        inner join oxpl.tbl_int_buss_line as n
        on m.buss_line = n.cod
        where m.cheie = a.id
        for json path) as nvarchar(max)) as valori
    from oxpl.tbl_int_key_head as a
    inner join oxpl.tbl_int_ccntr as b
    on a.hier = b.hier and a.data_set = b.data_set and a.cost_centre = b.cod
    where a.ktype='S01' and a.id = @id;