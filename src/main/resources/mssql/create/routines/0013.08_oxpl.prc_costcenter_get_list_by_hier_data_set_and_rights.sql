create or alter procedure oxpl.prc_costcenter_get_list_by_hier_data_set_and_rights
    @hier char(5),
    @set int,
    @kid varchar(20)
as
    begin
        set nocount on;

        /* pregatire centre de cost cu drepturi */
        drop table if exists #ccntr_leaf;

        select
            a.id,
            a.hier,
            a.data_set,
            a.cod,
            a.nume,
            a.grup,
            a.blocat,
            cast(1 as bit) as leaf,
            a.mod_de,
            a.mod_timp
        into #ccntr_leaf
        from oxpl.tbl_int_ccntr as a
        inner join oxpl.tbl_int_users_cost_centers as b
        on a.hier = b.hier and a.cod = b.cost_centre
        where a.hier = @hier and a.data_set = @set and b.uname = @kid;

        /* pregatire grupuri de centre de cost cu drepturi */
        drop table if exists #ccntr_group;

        with cte as (
            select
                a.id,
                a.hier,
                a.data_set,
                a.cod,
                a.nume,
                a.superior,
                a.mod_de,
                a.mod_timp
            from oxpl.tbl_int_ccntr_grup as a
            inner join #ccntr_leaf as b
            on a.hier = b.hier and a.data_set = b.data_set and a.cod = b.grup

            union all

            select
                a.id,
                a.hier,
                a.data_set,
                a.cod,
                a.nume,
                a.superior,
                a.mod_de,
                a.mod_timp
            from oxpl.tbl_int_ccntr_grup as a
            inner join cte as b
            on a.id = b.superior)
        select distinct
            *
        into #ccntr_group
        from cte as a;

        /* pregatire rezultat */
        with cte as (
            select
                a.*,
                cast(0 as tinyint) as nivel
            from #ccntr_group as a
            where a.superior is null

            union all

            select
                a.*,
                cast((b.nivel + 1) as tinyint) as nivel
            from #ccntr_group as a
            inner join cte as b
            on a.superior = b.id
        )
        select * from
            (select
                a.id,
                a.hier,
                a.data_set,
                a.cod,
                a.nume,
                b.cod as grup,
                cast(0 as bit) as blocat,
                cast(0 as bit) as leaf,
                a.nivel,
                a.mod_de,
                a.mod_timp
            from cte as a
            left join cte as b
            on a.superior = b.id

            union

            select
                a.id,
                a.hier,
                a.data_set,
                a.cod,
                a.nume,
                a.grup,
                a.blocat,
                a.leaf,
                cast((b.nivel + 1) as smallint) as nivel,
                a.mod_de,
                a.mod_timp
            from #ccntr_leaf as a
            inner join cte as b
            on a.grup = b.cod) as rsl
        order by rsl.nivel asc, rsl.cod asc;
    end;