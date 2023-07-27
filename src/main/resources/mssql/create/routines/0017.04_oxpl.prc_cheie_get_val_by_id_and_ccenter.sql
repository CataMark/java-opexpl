create or alter procedure oxpl.prc_cheie_get_val_by_id_and_ccenter
    @id int,
    @set int,
    @cost_centre varchar(10)
as
    select
        a.*,
        b.seg_ind as buss_line_seg,
        b.nume as buss_line_nume
    from oxpl.tbl_int_key_vals as a
    
    inner join oxpl.tbl_int_buss_line as b
    on a.buss_line = b.cod

    where a.cheie = @id and a.gen_data_set = @set and
        1 = (case when a.cost_centre is null then 1 else iif(a.cost_centre = @cost_centre, 1, 0) end);