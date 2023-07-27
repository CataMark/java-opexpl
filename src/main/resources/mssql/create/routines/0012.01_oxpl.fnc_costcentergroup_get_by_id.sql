create or alter function oxpl.fnc_costcentergroup_get_by_id(
    @id uniqueidentifier
)
returns table
as
return
    select top 1
        a.*,
        c.cod as superior_cod,
        c.nume as superior_nume
    from oxpl.tbl_int_ccntr_grup as a
    left join oxpl.tbl_int_ccntr_grup as c
    on a.superior = c.id
    where a.id = @id;