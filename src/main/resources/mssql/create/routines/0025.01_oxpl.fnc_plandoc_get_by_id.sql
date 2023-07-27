create or alter function oxpl.fnc_plandoc_get_by_id(
    @id uniqueidentifier
)
returns table
as
return
    select
        a.*,
        b.nume as cost_centre_nume,
        b.blocat as cost_centre_blocat,
        c.nume as cheie_nume,
        c.blocat as cheie_blocat,
        d.cost_driver,
        e.nume as cost_driver_nume,
        e.central as cost_driver_central,
        d.nume as opex_categ_nume,
        f.blocat as opex_categ_blocat,
        g.nume as ic_part_nume,
        cast((case a.coarea when g.coarea then 1 else 0 end) as bit) as ic_part_blocat,
        cast((select distinct
            first_value(case m.cont when d.cont_ccoa then m.id else null end) over (partition by m.an, m.per order by (case m.cont when d.cont_ccoa then m.id else null end) desc
                rows between unbounded preceding and unbounded following) as id,
            m.head_id,
            d.cont_ccoa as cont,
            m.data_set,
            m.an,
            m.per,
            n.actual,
            sum(m.valoare) over (partition by m.an, m.per) as valoare,
            first_value(m.mod_de) over (partition by m.head_id order by m.mod_timp desc rows between unbounded preceding and unbounded following) as mod_de,
            max(m.mod_timp) over (partition by m.head_id) as mod_timp
        from oxpl.tbl_int_recs_plan_vals as m
        inner join oxpl.tbl_int_data_set_per as n
        on m.data_set = n.data_set and m.an = n.an and m.per = n.per
        where m.head_id = a.id
        for json path) as nvarchar(max)) as valori
    from oxpl.tbl_int_recs_plan_head as a

    inner join oxpl.tbl_int_ccntr as b
    on a.data_set = b.data_set and a.hier = b.hier and a.cost_centre = b.cod

    left join oxpl.tbl_int_key_head as c
    on a.cheie = c.id

    inner join oxpl.tbl_int_opex_categ as d
    on a.opex_categ = d.cod

    inner join oxpl.tbl_int_cost_driver as e
    on d.cost_driver = e.cod

    inner join oxpl.tbl_int_opex_categ_assign as f
    on a.coarea = f.coarea and a.opex_categ = f.opex_categ

    left join oxpl.tbl_int_ic_part as g
    on a.ic_part = g.cod

    where a.id = @id;