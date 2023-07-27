create or alter procedure oxpl.prc_plandoc_get_list_ccenter_ncentr_aloc
    @set int,
    @hier char(5),
    @cost_centre varchar(10),
    @cdriver_central bit = null,
    @kid varchar(20)
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
            d.cost_driver,
            e.nume as cost_driver_nume,
            e.central as cost_driver_central,
            a.opex_categ,
            d.nume as opex_categ_nume,
            f.blocat as opex_categ_blocat,
            a.ic_part,
            g.nume as ic_part_nume,
            cast((case when a.coarea = g.coarea then 1 else 0 end) as bit) as ic_part_blocat,
            b.an,
            sum(b.valoare) as valoare
        into #plan_doc_recs
        from oxpl.tbl_int_recs_plan_head as a

        inner join oxpl.tbl_int_recs_plan_vals as b
        on a.id = b.head_id

        inner join oxpl.fnc_hier_get_childs(@hier, @set, @cost_centre, @kid) as c
        on a.cost_centre = c.cod

        inner join oxpl.tbl_int_opex_categ as d
        on a.opex_categ = d.cod

        inner join oxpl.tbl_int_cost_driver as e
        on d.cost_driver = e.cod

        inner join oxpl.tbl_int_opex_categ_assign as f
        on a.opex_categ = f.opex_categ and a.coarea = f.coarea

        left join oxpl.tbl_int_ic_part as g
        on a.ic_part = g.cod

        where a.data_set = @set and a.hier = @hier and
            1 = (case when @cdriver_central is null then 1 else iif(e.central = @cdriver_central, 1, 0) end)
        group by a.coarea, a.hier, a.data_set, a.cost_centre, a.cheie, d.cost_driver, e.nume, e.central, a.opex_categ, d.nume, f.blocat,
                a.ic_part, g.nume, cast((case when a.coarea = g.coarea then 1 else 0 end) as bit), b.an;

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
        select
            a.*,
            cast((select
                    *
                from
                    (select
                        'planificat' as cont,
                        m.an,
                        null as per,
                        sum(valoare) as valoare
                    from #plan_doc_recs as m
                    where m.cost_driver = a.cost_driver and m.opex_categ = a.opex_categ and (m.ic_part = a.ic_part or (m.ic_part is null and a.ic_part is null))
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
                    where m.cost_driver = a.cost_driver and m.opex_categ = a.opex_categ and (m.ic_part = a.ic_part or (m.ic_part is null and a.ic_part is null))
                    group by n.buss_line, m.an) as x
                order by x.cont desc, x.an asc
                for json path) as nvarchar(max)) as valori
        from
            (select distinct
                m.coarea,
                m.hier,
                m.data_set,
                @cost_centre as cost_centre,
                m.cost_driver,
                m.cost_driver_nume,
                m.cost_driver_central,
                m.opex_categ,
                m.opex_categ_nume,
                m.opex_categ_blocat,
                m.ic_part,
                m.ic_part_nume,
                m.ic_part_blocat
            from #plan_doc_recs as m) as a
        order by a.cost_driver_central asc, a.cost_driver asc, a.opex_categ asc, a.ic_part asc;
    end;