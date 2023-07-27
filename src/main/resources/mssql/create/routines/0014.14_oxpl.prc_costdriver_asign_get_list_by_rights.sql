create or alter procedure oxpl.prc_costdriver_asign_get_list_by_rights
    @coarea char(4),
    @kid varchar(20)
as
    select
        b.id,
        a.cod,
        a.nume,
        a.central,
        b.blocat,
        b.mod_de,
        b.mod_timp
    from oxpl.tbl_int_cost_driver as a

    inner join oxpl.tbl_int_cost_driver_assign as b
    on a.cod = b.cost_driver

    inner join oxpl.tbl_int_users_cost_drivers as c
    on b.coarea = c.coarea and b.cost_driver = c.cost_driver

    where b.coarea = @coarea and c.uname = @kid
    order by a.cod asc;