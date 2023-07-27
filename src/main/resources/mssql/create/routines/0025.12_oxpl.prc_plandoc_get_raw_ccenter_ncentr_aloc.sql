create or alter procedure oxpl.prc_plandoc_get_raw_ccenter_ncentr_aloc
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
            a.id as doc_id,
            trim(replace(replace(replace(a.descr,char(13),' '), char(10), ' '), '"','')) as descr,
            a.coarea,
            a.hier,
            a.data_set,
            a.cost_centre,
            c.nume as cost_centre_nume,
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
            (case when b.cont is null then d.cont_ccoa else b.cont end) as cont,
            b.an,
            b.per,
            h.actual,
            b.valoare,
            a.mod_de as doc_mod_de,
            a.mod_timp as doc_mod_timp,
            b.mod_de as val_mod_de,
            b.mod_timp as val_mod_timp
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

        inner join oxpl.tbl_int_data_set_per as h
        on b.data_set = h.data_set and b.an = h.an and b.per = h.per

        where a.data_set = @set and a.hier = @hier and
            1 = (case when @cdriver_central is null then 1 else iif(e.central = @cdriver_central, 1, 0) end);

        /* calcul procente de alocare */
        drop table if exists #plan_key_prc;

        select
            a.cheie,
            a.cheie_nume,
            a.cost_centre,
            a.an,
            b.buss_line,
            c.seg_ind as buss_line_seg,
            c.nume as buss_line_nume,
            round(b.valoare / a.total, 6) as procent
        into #plan_key_prc
        from
            (select
                m.cheie,
                m.cheie_nume,
                n.gen_data_set,
                m.cost_centre,
                n.an,
                sum(n.valoare) as total
            from (select distinct
                    a.cheie,
                    b.nume as cheie_nume,
                    (case when b.ktype in ('G01', 'S01') then a.cost_centre else null end) as cost_centre
                from #plan_doc_recs as a
                inner join oxpl.tbl_int_key_head as b
                on a.cheie = b.id) as m

            inner join oxpl.tbl_int_key_vals as n
            on m.cheie = n.cheie and (m.cost_centre = n.cost_centre or (m.cost_centre is null and n.cost_centre is null))

            where n.gen_data_set = @set
            group by m.cheie, m.cheie_nume, n.gen_data_set, m.cost_centre, n.an) as a

        inner join oxpl.tbl_int_key_vals as b
        on a.cheie = b.cheie and a.gen_data_set = b.gen_data_set and (a.cost_centre = b.cost_centre or (a.cost_centre is null and b.cost_centre is null)) and
            a.an = b.an

        inner join oxpl.tbl_int_buss_line as c
        on b.buss_line = c.cod

        where a.total != 0;

        /* obtine alocare valori planificate */
        select
            *
        from
            (select
                a.doc_id,
                a.descr,
                a.coarea,
                a.hier,
                a.data_set,
                a.cost_centre,
                a.cost_centre_nume,
                null as cheie,
                null as cheie_nume,
                a.cost_driver,
                a.cost_driver_nume,
                a.cost_driver_central,
                a.opex_categ,
                a.opex_categ_nume,
                a.opex_categ_blocat,
                a.ic_part,
                a.ic_part_nume,
                a.ic_part_blocat,
                'planificat' as tip,
                a.cont,
                a.an,
                a.per,
                a.actual,
                null as buss_line,
                null as buss_line_seg,
                null as buss_line_nume,
                a.valoare,
                a.doc_mod_de,
                a.doc_mod_timp,
                a.val_mod_de,
                a.val_mod_timp
            from #plan_doc_recs as a
            
            union all
            
            select
                a.doc_id,
                a.descr,
                a.coarea,
                a.hier,
                a.data_set,
                a.cost_centre,
                a.cost_centre_nume,
                b.cheie,
                b.cheie_nume,
                a.cost_driver,
                a.cost_driver_nume,
                a.cost_driver_central,
                a.opex_categ,
                a.opex_categ_nume,
                a.opex_categ_blocat,
                a.ic_part,
                a.ic_part_nume,
                a.ic_part_blocat,
                'alocat' as tip,
                a.cont,
                a.an,
                a.per,
                a.actual,
                b.buss_line,
                b.buss_line_seg,
                b.buss_line_nume,
                (a.valoare * b.procent) as valoare,
                null as doc_mod_de,
                null as doc_mod_timp,
                null as val_mod_de,
                null as val_mod_timp
            from #plan_doc_recs as a
            inner join #plan_key_prc as b
            on a.cheie = b.cheie and a.an = b.an and (a.cost_centre = b.cost_centre or b.cost_centre is null)) as x;
    end;