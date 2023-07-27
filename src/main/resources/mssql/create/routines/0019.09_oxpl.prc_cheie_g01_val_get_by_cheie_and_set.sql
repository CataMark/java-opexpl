create or alter procedure oxpl.prc_cheie_g01_val_get_by_cheie_and_set
    @cheie int,
    @set int
as
    select
        a.id,
        a.nume,
        a.descr,
        a.coarea,
        b.data_set,
        a.ktype,
        a.blocat,
        b.buss_line,
        c.seg_ind as buss_line_seg,
        c.nume as buss_line_nume,
        b.cost_centre,
        d.nume as cost_centre_nume,
        b.an,
        b.valoare,
        a.mod_de as def_mod_de,
        a.mod_timp as def_mod_timp,
        b.mod_de as val_mod_de,
        b.mod_timp as val_mod_timp
    from oxpl.tbl_int_key_head as a

    inner join oxpl.tbl_int_key_vals as b
    on a.id = b.cheie

    inner join oxpl.tbl_int_buss_line as c
    on b.buss_line = c.cod

    inner join oxpl.tbl_int_ccntr as d
    on b.hier = d.hier and b.data_set = d.data_set and b.cost_centre = d.cod

    where a.id = @cheie and b.gen_data_set = @set;