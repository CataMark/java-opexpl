/**
    functia obtine centrele de cost copil, pentru care exista drepturi de introducere inregistrari,
    pana la ultimul nivel, din cadrul grupului de centre de cost input;
    daca input este un centru de cost atunci functia va returna
    acelasi centru de cost
*/

create or alter function oxpl.fnc_hier_get_childs(
    @hier char(5),
    @data_set int,
    @cost_centre varchar(10),
    @kid varchar(20) = null
)
returns @rezultat table(cod varchar(10), nume nvarchar(100))
as
    begin
        declare @groups table(cod varchar(10), nume nvarchar(100));       

        with cte as (
            select a.id, a.cod, a.nume, a.superior
            from oxpl.tbl_int_ccntr_grup as a
            where a.hier = @hier and a.data_set = @data_set and a.cod = @cost_centre

            union all

            select a.id, a.cod, a.nume, a.superior
            from oxpl.tbl_int_ccntr_grup as a
            inner join cte as b
            on a.superior = b.id
        )
        insert into @groups(cod, nume)
        select a.cod, a.nume
        from cte as a;

        declare @cntr_bound bit;
        if @kid is null
            set @cntr_bound = 0;
        else
            select top 1 @cntr_bound = coalesce(b.cost_center_bound, 1)
            from portal.tbl_int_ugroups as a
            inner join oxpl.tbl_int_ugrup as b
            on a.ugroup = b.cod
            where a.uname = @kid and b.cost_driver_bound != 1
            order by b.ordine asc;

        if @cntr_bound = 0
            if exists (select * from @groups)
                insert into @rezultat(cod, nume)
                select a.cod, a.nume
                from oxpl.tbl_int_ccntr as a

                inner join @groups as b
                on a.grup = b.cod

                where a.hier = @hier and a.data_set = @data_set;
            else
                insert into @rezultat(cod, nume)
                select a.cod, a.nume
                from oxpl.tbl_int_ccntr as a
                where a.hier = @hier and a.data_set = @data_set and a.cod = @cost_centre;
        else
            if exists (select * from @groups)
                insert into @rezultat(cod, nume)
                select a.cod, a.nume
                from oxpl.tbl_int_ccntr as a

                inner join @groups as b
                on a.grup = b.cod

                inner join oxpl.tbl_int_users_cost_centers as c
                on a.hier = c.hier and a.cod = c.cost_centre

                where a.hier = @hier and a.data_set = @data_set and c.uname = @kid;
            else
                insert into @rezultat(cod, nume)
                select a.cod, a.nume
                from oxpl.tbl_int_ccntr as a

                inner join oxpl.tbl_int_users_cost_centers as b
                on a.hier = b.hier and a.cod = b.cost_centre

                where a.hier = @hier and a.data_set = @data_set and a.cod = @cost_centre and b.uname = @kid;

        return;
    end;