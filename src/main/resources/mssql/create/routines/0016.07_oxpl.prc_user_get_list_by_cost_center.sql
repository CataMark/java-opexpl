create or alter procedure oxpl.prc_user_get_list_by_cost_center
    @hier char(5),
    @set int,
    @cost_centre varchar(10),
    @kid varchar(20)
as
    select distinct
        b.uname,
        a.nume,
        a.prenume,
        first_value(b.mod_de) over (partition by b.uname order by b.mod_timp desc rows between unbounded preceding and unbounded following) as mod_de,
        first_value(b.mod_timp) over (partition by b.uname order by b.mod_timp desc rows between unbounded preceding and unbounded following) as mod_timp
    from portal.tbl_int_users as a

    right join oxpl.tbl_int_users_cost_centers as b
    on a.uname = b.uname

    inner join (select cod, nume from oxpl.fnc_hier_get_childs(@hier, @set, @cost_centre, @kid)) as c
    on b.cost_centre = c.cod

    where b.hier = @hier
    order by b.uname asc;