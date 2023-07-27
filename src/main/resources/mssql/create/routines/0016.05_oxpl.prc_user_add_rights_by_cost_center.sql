create or alter procedure oxpl.prc_user_add_rights_by_cost_center
    @uname varchar(20),
    @hier char(5),
    @set int,
    @cost_centre varchar(10),
    @kid varchar(20)
as
    begin
        set nocount on;

        declare @result table (uname varchar(20), mod_de varchar(20), mod_timp datetime);

        merge into oxpl.tbl_int_users_cost_centers as tinta
        using (select
                @uname as uname,
                @hier as hier,
                @kid as mod_de,
                cod as cost_centre
                from oxpl.fnc_hier_get_childs(@hier, @set, @cost_centre, @kid)) as sursa
        on (tinta.uname = sursa.uname and tinta.hier = sursa.hier and tinta.cost_centre = sursa.cost_centre)
        when not matched then
            insert (uname, hier, cost_centre, mod_de, mod_timp)
            values (sursa.uname, sursa.hier, sursa.cost_centre, sursa.mod_de, current_timestamp)
            output inserted.uname, inserted.mod_de, inserted.mod_timp into @result(uname, mod_de, mod_timp);

        select a.uname, b.nume, b.prenume, a.mod_de, a.mod_timp
        from (select top 1 * from @result) as a
        left join portal.tbl_int_users as b
        on a.uname = b.uname;
    end;