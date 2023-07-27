create or alter function oxpl.fnc_costdriver_asign_get_by_id(
    @id uniqueidentifier
)
returns table
as
return
    select top 1 a.id, b.cod, b.nume, b.central, a.blocat, a.mod_de, a.mod_timp
    from oxpl.tbl_int_cost_driver_assign as a
    inner join oxpl.tbl_int_cost_driver as b
    on a.cost_driver = b.cod
    where a.id = @id;