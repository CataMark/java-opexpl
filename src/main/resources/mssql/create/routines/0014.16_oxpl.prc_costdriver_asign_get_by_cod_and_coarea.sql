create or alter procedure oxpl.prc_costdriver_asign_get_by_cod_and_coarea
    @cod char(5),
    @coarea char(4)
as
    select
        a.id,
        b.cod,
        b.nume,
        b.central,
        a.blocat,
        a.mod_de,
        a.mod_timp
    from oxpl.tbl_int_cost_driver_assign as a
    inner join oxpl.tbl_int_cost_driver as b
    on a.cost_driver = b.cod
    where a.coarea = @coarea and a.cost_driver = @cod;