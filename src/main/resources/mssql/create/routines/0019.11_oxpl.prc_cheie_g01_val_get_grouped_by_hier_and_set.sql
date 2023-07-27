create or alter procedure oxpl.prc_cheie_g01_val_get_grouped_by_hier_and_set
    @cheie int,
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
            cast(0 as tinyint) as nivel
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
            cast((b.nivel + 1) as tinyint) as nivel
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
            cast(null as nvarchar(max)) as valori        
        from cte as a
        
        union all

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
            cast((select distinct
                    m.an,
                    sum(m.valoare) over (partition by m.an) as valoare,
                    first_value(m.mod_de) over (partition by m.cost_centre order by m.mod_timp desc rows between unbounded preceding and unbounded following) as mod_de,
                    max(m.mod_timp) over (partition by m.cost_centre) as mod_timp
                from oxpl.tbl_int_key_vals as m
                where m.cheie = @cheie and m.gen_data_set = @set and m.hier = a.hier and m.cost_centre = a.cod
                order by m.an asc
                for json path) as nvarchar(max)) as valori
        from oxpl.tbl_int_ccntr as a
        inner join cte as b
        on a.hier = b.hier and a.data_set = b.data_set and a.grup = b.cod
        where a.hier = @hier and a.data_set = @set) as rsl
    order by nivel asc, cod asc;