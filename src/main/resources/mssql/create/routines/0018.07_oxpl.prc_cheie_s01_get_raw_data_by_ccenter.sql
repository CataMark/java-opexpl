create or alter procedure oxpl.prc_cheie_s01_get_raw_data_by_ccenter
    @hier char(5),
    @set int,
    @cost_centre varchar(10),
    @kid varchar(20)
as
    select
        b.cheie,
        a.ktype,
        a.nume,
        a.descr,
        a.coarea,
        a.data_set,
        a.cost_centre,
        c.nume as cost_centre_nume,
        b.buss_line,
        d.seg_ind as buss_line_seg,
        d.nume as buss_line_nume,
        b.an,
        b.valoare,
        a.mod_de as key_mod_de,
        a.mod_timp as key_mod_timp,
        b.mod_de as val_mod_de,
        b.mod_timp as val_mod_timp
    from oxpl.tbl_int_key_head as a
    inner join oxpl.tbl_int_key_vals as b
    on a.id = b.cheie
    inner join (select cod, nume from oxpl.fnc_hier_get_childs(@hier, @set, @cost_centre, @kid)) as c
    on a.cost_centre = c.cod
    inner join oxpl.tbl_int_buss_line as d
    on b.buss_line = d.cod
    where a.ktype = 'S01' and a.hier = @hier and a.data_set = @set;