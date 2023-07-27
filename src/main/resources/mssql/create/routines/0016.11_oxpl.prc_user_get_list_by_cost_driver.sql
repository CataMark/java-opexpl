create or alter procedure oxpl.prc_user_get_list_by_cost_driver
    @coarea char(4),
    @cost_driver char(5)
as
    select
        a.uname,
        b.prenume,
        b.nume,
        a.mod_de,
        a.mod_timp
    from oxpl.tbl_int_users_cost_drivers as a
    left join portal.tbl_int_users as b
    on a.uname = b.uname
    where a.coarea = @coarea and a.cost_driver = @cost_driver
    order by a.uname;