create or alter procedure oxpl.prc_costcenter_get_by_cod
    @hier char(5),
    @set int,
    @cod varchar(10)
as
    select top 1
        a.*,
        b.nume as grup_nume,
        cast(1 as bit) as leaf
    from oxpl.tbl_int_ccntr as a
    inner join oxpl.tbl_int_ccntr_grup as b
    on a.grup = b.cod and a.hier = b.hier and a.data_set = b.data_set
    where a.hier = @hier and a.data_set = @set and a.cod = @cod;