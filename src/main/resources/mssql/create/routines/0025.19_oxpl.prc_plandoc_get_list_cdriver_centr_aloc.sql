create or alter procedure oxpl.prc_plandoc_get_list_cdriver_centr_aloc
    @set int,
    @hier char(5),
    @cdriver char(5),
    @ocateg int = null
as
    begin
        set nocount on;

        /* stabilire inregistrari */
        drop table if exists #plan_doc_recs;

        select
            a.coarea,
            a.hier,
            a.data_set,
            a.cost_centre,
            a.cheie,
            c.cost_driver,
            a.opex_categ,
            c.nume as opex_categ_nume,
            d.blocat as opex_categ_blocat,
            a.ic_part,
            e.nume as ic_part_nume,
            cast((case when a.coarea = e.coarea then 1 else 0 end) as bit) as ic_part_blocat,
            b.an,
            sum(b.valoare) as valoare
        into #plan_doc_recs
        from oxpl.tbl_int_recs_plan_head as a

        inner join oxpl.tbl_int_recs_plan_vals as b
        on a.id = b.head_id

        inner join oxpl.tbl_int_opex_categ as c
        on a.opex_categ = c.cod

        inner join oxpl.tbl_int_opex_categ_assign as d
        on a.coarea = d.coarea and a.opex_categ = d.opex_categ

        left join oxpl.tbl_int_ic_part as e
        on a.ic_part = e.cod

        where a.data_set = @set and a.hier = @hier and c.cost_driver = @cdriver and
            1 = (case when @ocateg is null then 1 else iif(a.opex_categ = @ocateg, 1, 0) end)
        group by a.coarea, a.hier, a.data_set, a.cost_centre, a.cheie, c.cost_driver, a.opex_categ, c.nume, d.blocat,
            a.ic_part, e.nume, cast((case when a.coarea = e.coarea then 1 else 0 end) as bit), b.an;

        /* calcul procente de alocare */
        drop table if exists #plan_key_prc;

        select
            a.cheie,
            a.cost_centre,
            a.an,
            b.buss_line,
            (b.valoare / a.total) as procent
        into #plan_key_prc
        from
            (select
                m.cheie,
                n.gen_data_set,
                m.cost_centre,
                n.an,
                sum(n.valoare) as total
            from (select distinct
                    a.cheie,
                    (case when b.ktype in ('G01', 'S01') then a.cost_centre else null end) as cost_centre
                from #plan_doc_recs as a
                inner join oxpl.tbl_int_key_head as b
                on a.cheie = b.id) as m

            inner join oxpl.tbl_int_key_vals as n
            on m.cheie = n.cheie and (m.cost_centre = n.cost_centre or (m.cost_centre is null and n.cost_centre is null))

            where n.gen_data_set = @set
            group by m.cheie, n.gen_data_set, m.cost_centre, n.an) as a

        inner join oxpl.tbl_int_key_vals as b
        on a.cheie = b.cheie and a.gen_data_set = b.gen_data_set and (a.cost_centre = b.cost_centre or (a.cost_centre is null and b.cost_centre is null)) and
            a.an = b.an

        where a.total != 0;

        /* alocare valori planificate */
        if @ocateg is null /* versiune fara categorie de cheltuieli setata */
            select
                a.*,
                cast((select
                        *
                    from (select
                            'planificat' as cont,
                            m.an,
                            null as per,
                            sum(m.valoare) as valoare
                        from #plan_doc_recs as m
                        where m.opex_categ = a.opex_categ and (m.ic_part = a.ic_part or (m.ic_part is null and a.ic_part is null))
                        group by m.an
                        
                        union all
                        
                        select
                            'alocat' as cont,
                            m.an,
                            n.buss_line as per,
                            sum(m.valoare * n.procent) as valoare
                        from #plan_doc_recs as m
                        inner join #plan_key_prc as n
                        on m.cheie = n.cheie and m.an = n.an and (m.cost_centre = n.cost_centre or n.cost_centre is null)
                        where m.opex_categ = a.opex_categ and (m.ic_part = a.ic_part or (m.ic_part is null and a.ic_part is null))
                        group by n.buss_line, m.an) as x
                    order by x.cont desc, x.an asc
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
                from #plan_doc_recs as m) as a
            order by a.opex_categ asc, a.ic_part asc;
        
        else /* versiune cu categorie de cheltuieli */

            /* stabilire centre de cost cu valori */
            drop table if exists #ccntr_leaf;

            select
                a.id,
                a.hier,
                a.data_set,
                a.cod,
                a.nume,
                a.grup,
                a.blocat,
                cast(1 as bit) as leaf
            into #ccntr_leaf
            from oxpl.tbl_int_ccntr as a
            inner join (select distinct hier, data_set, cost_centre from #plan_doc_recs) as b
            on a.hier = b.hier and a.data_set = b.data_set and a.cod = b.cost_centre;

            /* stabilire grupuri de centre de cost cu valori */
            drop table if exists #ccntr_group;

            with cte as(
                select
                    a.id,
                    a.hier,
                    a.data_set,
                    a.cod,
                    a.nume,
                    a.superior
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
                    a.superior
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
                    a.hier,
                    a.data_set,
                    a.cod as cost_centre,
                    a.nume as cost_centre_nume,
                    b.cod as cost_centre_super,
                    cast(0 as bit) as cost_centre_blocat,
                    cast(0 as bit) as cost_centre_leaf,
                    a.nivel as cost_centre_nivel,
                    @cdriver as cost_driver,
                    @ocateg as opex_categ,
                    null as ic_part,
                    null as ic_part_nume,
                    null as ic_part_blocat,
                    cast(null as nvarchar(max)) as valori
                from cte as a
                left join cte as b
                on a.superior = b.id
                
                union all
                
                select
                    a.hier,
                    a.data_set,
                    a.cod as cost_centre,
                    a.nume as cost_centre_nume,
                    a.grup as cost_centre_super,
                    a.blocat as cost_centre_blocat,
                    a.leaf as cost_centre_leaf,
                    cast((b.nivel + 1) as tinyint) as cost_centre_nivel,
                    @cdriver as cost_driver,
                    @ocateg as opex_categ,
                    c.ic_part,
                    c.ic_part_nume,
                    c.ic_part_blocat,
                    cast((select
                            *
                        from (select
                                'planificat' as cont,
                                m.an,
                                null as per,
                                sum(m.valoare) as valoare
                            from #plan_doc_recs as m
                            where m.cost_centre = a.cod and (m.ic_part = c.ic_part or (m.ic_part is null and c.ic_part is null))
                            group by m.an
                            
                            union all
                            
                            select
                                'alocat' as cont,
                                m.an,
                                n.buss_line as per,
                                sum(m.valoare * n.procent) as valoare
                            from #plan_doc_recs as m
                            inner join #plan_key_prc as n
                            on m.cheie = n.cheie and m.an = n.an and (m.cost_centre = n.cost_centre or n.cost_centre is null)
                            where m.cost_centre = a.cod and (m.ic_part = c.ic_part or (m.ic_part is null and c.ic_part is null))
                            group by n.buss_line, m.an) as x
                        order by x.cont desc, x.an asc
                        for json path) as nvarchar(max)) as valori
                from #ccntr_leaf as a

                inner join cte as b
                on a.grup = b.cod
                
                inner join (select distinct
                                m.cost_centre,
                                m.ic_part,
                                m.ic_part_nume,
                                m.ic_part_blocat
                            from #plan_doc_recs as m) as c
                on a.cod = c.cost_centre) as rsl
            order by cost_centre_nivel asc, cost_centre asc, ic_part asc;
    end;