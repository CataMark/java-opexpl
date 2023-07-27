create or alter procedure oxpl.prc_costcenter_get_list_by_hier_and_data_set
    @set int,
    @hier char(5)
as
    with cte as (
        select
            a.id,
            a.hier,
            a.data_set,
            a.cod,
            a.nume,
            a.superior,
            cast(null as varchar(10)) as superior_cod,
            cast(null as nvarchar(50)) as superior_nume,
            cast(0 as tinyint) as nivel,
            a.mod_de,
            a.mod_timp
        from oxpl.tbl_int_ccntr_grup as a
        where a.hier = @hier and a.data_set = @set and a.superior is null

        union all

        select
            a.id,
            a.hier,
            a.data_set,
            a.cod,
            a.nume,
            a.superior,
            b.cod as superior_cod,
            b.nume as superior_nume,
            cast((b.nivel + 1) as tinyint) as nivel,
            a.mod_de,
            a.mod_timp
        from oxpl.tbl_int_ccntr_grup as a
        inner join cte as b
        on a.superior = b.id)

    select * from
        (select
            a.id,
            a.hier,
            a.data_set,
            a.cod,
            a.nume,
            a.superior_cod as grup,
            cast(0 as bit) as blocat,
            cast(0 as bit) as leaf,
            a.nivel,
            a.mod_de,
            a.mod_timp
        from cte as a

        union

        select
            a.id,
            a.hier,
            a.data_set,
            a.cod,
            a.nume,
            a.grup,
            a.blocat,
            cast(1 as bit) as leaf,
            cast((b.nivel + 1) as tinyint) as nivel,
            a.mod_de,
            a.mod_timp
        from oxpl.tbl_int_ccntr as a
        inner join cte as b
        on a.hier = b.hier and a.data_set = b.data_set and a.grup = b.cod
        where a.hier = @hier and a.data_set = @set) as rsl

    order by nivel asc, cod asc;