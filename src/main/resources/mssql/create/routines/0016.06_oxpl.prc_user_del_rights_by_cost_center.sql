create or alter procedure oxpl.prc_user_del_rights_by_cost_center
    @uname varchar(20),
    @hier char(5),
    @set int,
    @cost_centre varchar(10),
    @kid varchar(20)
as
    begin
        set nocount on;
        
        declare @result table (cost_centre varchar(10));

        delete a
        output deleted.cost_centre into @result(cost_centre)
        from oxpl.tbl_int_users_cost_centers as a
        inner join (select cod from oxpl.fnc_hier_get_childs(@hier, @set, @cost_centre, @kid)) as b
        on a.cost_centre = b.cod
        where a.uname = @uname and a.hier = @hier;

        /* testare operatiune reusita */
        if exists (select * from @result)
            select cast(1 as bit) as rezultat;
        else
            select cast(0 as bit) as rezultat;
    end;