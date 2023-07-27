create or alter procedure oxpl.prc_costcentergroup_get_by_hier_data_set_and_cod
    @hier char(5),
    @set int,
    @cod varchar(10)
as
    select top 1
        a.*,
        c.cod as superior_cod,
        c.nume as superior_nume
    from oxpl.tbl_int_ccntr_grup as a
    left join oxpl.tbl_int_ccntr_grup as c
    on a.superior = c.id
    where a.hier = @hier and a.data_set = @set and a.cod = @cod;