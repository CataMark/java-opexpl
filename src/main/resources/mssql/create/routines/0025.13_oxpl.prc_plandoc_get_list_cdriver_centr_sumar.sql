create or alter procedure oxpl.prc_plandoc_get_list_cdriver_centr_sumar
    @set int,
    @hier char(5),
    @cdriver char(5),
    @ocateg int = null
as
    begin
        if @ocateg is null /* versiune fara categorie sql setata */
            with recs as (
                select
                    a.coarea,
                    a.hier,
                    a.data_set,
                    c.cost_driver,
                    a.opex_categ,
                    c.nume as opex_categ_nume,
                    d.blocat as opex_categ_blocat,
                    a.ic_part,
                    e.nume as ic_part_nume,
                    cast((case when a.coarea = e.coarea then 1 else 0 end) as bit) as ic_part_blocat,
                    b.an,
                    b.per,
                    sum(b.valoare) as valoare
                from oxpl.tbl_int_recs_plan_head as a

                inner join oxpl.tbl_int_recs_plan_vals as b
                on a.id = b.head_id

                inner join oxpl.tbl_int_opex_categ as c
                on a.opex_categ = c.cod

                inner join oxpl.tbl_int_opex_categ_assign as d
                on a.coarea = d.coarea and a.opex_categ = d.opex_categ

                left join oxpl.tbl_int_ic_part as e
                on a.ic_part = e.cod

                where a.data_set = @set and a.hier = @hier and c.cost_driver = @cdriver
                group by a.coarea, a.hier, a.data_set, c.cost_driver, a.opex_categ, c.nume, d.blocat,
                    a.ic_part, e.nume, cast((case when a.coarea = e.coarea then 1 else 0 end) as bit), b.an, b.per
            )
            select
                a.*,
                cast((select
                        n.an,
                        n.per,
                        n.valoare
                    from recs as n
                    where n.opex_categ = a.opex_categ and
                        (n.ic_part = a.ic_part or (n.ic_part is null and a.ic_part is null))
            order by n.an asc, n.per asc
            for json path) as nvarchar(max)) as valori
            from (select distinct
                    m.coarea,
                    m.hier,
                    m.data_set,
                    m.cost_driver,
                    m.opex_categ,
                    m.opex_categ_nume,
                    m.opex_categ_blocat,
                    m.ic_part,
                    m.ic_part_nume,
                    m.ic_part_blocat
                from recs as m) as a
            order by a.opex_categ asc, a.ic_part asc;

        else /* versiune cu categorie cheltuieli setata */
            with recs as (
                select
                    a.cost_centre,
                    a.ic_part,
                    c.nume as ic_part_nume,
                    cast((case when a.coarea = c.coarea then 1 else 0 end) as bit) as ic_part_blocat,
                    b.an,
                    b.per,
                    sum(b.valoare) as valoare
                from oxpl.tbl_int_recs_plan_head as a

                inner join oxpl.tbl_int_recs_plan_vals as b
                on a.id = b.head_id

                left join oxpl.tbl_int_ic_part as c
                on a.ic_part = c.cod

                where a.data_set = @set and a.hier = @hier and a.opex_categ = @ocateg
                group by a.cost_centre, a.ic_part, c.nume, cast((case when a.coarea = c.coarea then 1 else 0 end) as bit), b.an, b.per
            ),
            cte as (
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
                on a.superior = b.id
            )
            select * from
                (select
                    a.hier,
                    a.data_set,
                    a.cod as cost_centre,
                    a.nume as cost_centre_nume,
                    a.superior_cod as cost_centre_super,
                    cast(0 as bit) as cost_centre_blocat,
                    cast(0 as bit) as cost_centre_leaf,
                    a.nivel as cost_centre_nivel,
                    @cdriver as cost_driver,
                    @ocateg as opex_categ,
                    cast(null as varchar(5)) as ic_part,
                    cast(null as nvarchar(50)) as ic_part_nume,
                    cast(null as bit) as ic_part_blocat,
                    cast(null as nvarchar(max)) as valori        
                from cte as a

                union all

                select
                    a.hier,
                    a.data_set,
                    a.cod as cost_centre,
                    a.nume as cost_centre_nume,
                    a.grup as cost_centre_super,
                    a.blocat as cost_centre_blocat,
                    cast(1 as bit) as cost_centre_leaf,
                    cast((b.nivel + 1) as tinyint) as cost_centre_nivel,
                    @cdriver as cost_driver,
                    @ocateg as opex_categ,
                    c.ic_part,
                    c.ic_part_nume,
                    c.ic_part_blocat,
                    cast((select
                            m.an,
                            m.per,
                            m.valoare
                        from recs as m
                        where m.cost_centre = a.cod and
                            (m.ic_part = c.ic_part or (m.ic_part is null and c.ic_part is null))
                        order by m.an asc, m.per asc
                        for json path) as nvarchar(max)) as valori
                from oxpl.tbl_int_ccntr as a

                inner join cte as b
                on a.hier = b.hier and a.data_set = b.data_set and a.grup = b.cod

                left join (select distinct
                                m.cost_centre,
                                m.ic_part,
                                m.ic_part_nume,
                                m.ic_part_blocat
                            from recs as m) as c
                on a.cod = c.cost_centre

                where a.data_set = @set and a.hier = @hier) as rsl
            order by cost_centre_nivel asc, cost_centre asc, ic_part asc;
    end;
    