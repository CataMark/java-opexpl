create or alter procedure oxpl.prc_user_get_list_by_hier
    @hier char(5),
    @set int
as
    select
        a.uname as kid,
        b.nume,
        b.prenume,
        b.email,
        a.cost_centre,
        c.nume as cost_centre_nume,
        a.mod_de,
        a.mod_timp
    from oxpl.tbl_int_users_cost_centers as a

    left join portal.tbl_int_users as b
    on a.uname = b.uname

    left join oxpl.tbl_int_ccntr as c
    on a.hier = c.hier and a.cost_centre = c.cod

    where a.hier = @hier and c.data_set = @set
    order by a.uname asc;