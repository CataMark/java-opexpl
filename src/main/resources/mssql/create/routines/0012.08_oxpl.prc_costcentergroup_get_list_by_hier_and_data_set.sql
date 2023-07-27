create or alter procedure oxpl.prc_costcentergroup_get_list_by_hier_and_data_set
    @hier char(5),
    @set int
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

    select
        a.*
    from cte as a
    order by a.nivel asc, a.cod asc;