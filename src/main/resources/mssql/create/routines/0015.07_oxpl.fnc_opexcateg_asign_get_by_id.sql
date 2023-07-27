create or alter function oxpl.fnc_opexcateg_asign_get_by_id(
    @id uniqueidentifier
)
returns table
as
return
    select top 1
        a.id,
        b.cod,
        b.nume,
        b.cost_driver,
        c.nume as cost_driver_nume,
        b.cont_ccoa,
        a.blocat,
        a.mod_de,
        a.mod_timp
    from oxpl.tbl_int_opex_categ_assign as a
    inner join oxpl.tbl_int_opex_categ as b
    on a.opex_categ = b.cod
    inner join oxpl.tbl_int_cost_driver as c
    on b.cost_driver = c.cod
    where a.id = @id;