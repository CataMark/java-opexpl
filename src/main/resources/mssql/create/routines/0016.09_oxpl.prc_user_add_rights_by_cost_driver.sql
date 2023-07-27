create or alter procedure oxpl.prc_user_add_rights_by_cost_driver
    @uname varchar(20),
    @coarea char(4),
    @cost_driver char(5),
    @kid varchar(20)
as
    begin
        set nocount on;

        declare @id table (id uniqueidentifier);

        insert into oxpl.tbl_int_users_cost_drivers (uname, coarea, cost_driver, mod_de, mod_timp)
        output inserted.id into @id
        values (@uname, @coarea, @cost_driver, @kid, current_timestamp);

        select top 1
            a.uname,
            b.prenume,
            b.nume,
            a.mod_de,
            a.mod_timp
        from oxpl.tbl_int_users_cost_drivers as a

        left join portal.tbl_int_users as b
        on a.uname = b.uname

        inner join @id as c
        on a.id = c.id;
    end;