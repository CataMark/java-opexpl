create or alter procedure oxpl.prc_cheie_c01_get_raw_data_by_set
    @coarea char(4),
    @set int
as
    select
        b.cheie,
        a.ktype,
        a.nume,
        a.descr,
        a.coarea,
        b.buss_line,
        c.seg_ind as buss_line_seg,
        c.nume as buss_line_nume,
        b.an,
        b.valoare,
        a.mod_de as key_mod_de,
        a.mod_timp as key_mod_timp,
        b.mod_de as val_mod_de,
        b.mod_timp as val_mod_timp
    from oxpl.tbl_int_key_head as a

    inner join oxpl.tbl_int_key_vals as b
    on a.id = b.cheie

    inner join oxpl.tbl_int_buss_line as c
    on b.buss_line = c.cod

    where a.ktype = 'C01' and a.coarea = @coarea and b.gen_data_set = @set;