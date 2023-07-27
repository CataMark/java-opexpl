create or alter procedure oxpl.prc_cheie_get_list_gen_def_all
    @coarea char(4)
as
    select
        a.blocat,
        a.ktype,
        a.id,
        a.nume
    from oxpl.tbl_int_key_head as a
    inner join oxpl.tbl_int_key_type as b
    on a.ktype = b.cod
    where b.general = 1 and b.calculat = 0 and a.coarea = @coarea
    order by a.nume asc;