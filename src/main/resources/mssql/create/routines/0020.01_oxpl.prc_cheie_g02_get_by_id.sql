create or alter procedure oxpl.prc_cheie_g02_get_by_id
    @id int,
    @set int
as
    select top 1
        a.*,
        cast((select
            m.*,
            n.seg_ind as buss_line_seg,
            n.nume as buss_line_nume
        from oxpl.tbl_int_key_vals as m
        inner join oxpl.tbl_int_buss_line as n
        on m.buss_line = n.cod
        where m.cheie = a.id and m.gen_data_set = @set
        for json path) as nvarchar(max)) as valori
    from oxpl.tbl_int_key_head as a
    where a.ktype = 'G02' and a.id = @id;