create or alter procedure oxpl.prc_mdl_save_alocare
    @set int,
    @coarea char(4)
as
    begin
        set nocount on;
        begin try
            declare @has_alocare bit = 0;
            select @has_alocare = a.alocare from oxpl.tbl_int_coarea as a where a.cod = @coarea;

            begin transaction
                /* stergere valori calculate anterior */
                delete from oxpl.tbl_mdl_alocare where data_set = @set and coarea = @coarea;

                /* salvare inregistrari planificate in tabela de model */
                insert into oxpl.tbl_mdl_alocare(doc_id, coarea, coarea_nume, coarea_acronim, hier, descr, data_set, data_set_nume, data_set_vers, data_set_an,
                                                cost_centre, cost_centre_nume, cost_centre_blocat, cheie, cheie_nume, cheie_descr, cheie_tip, cost_driver, cost_driver_nume, cost_driver_central,
                                                opex_categ, opex_categ_nume, ic_part, ic_part_nume, val_id, val_tip, actual, cont, buss_line, buss_line_seg, buss_line_nume,
                                                an, per, valoare, doc_mod_de, doc_mod_timp, val_mod_de, val_mod_timp)
                select
                    a.id as doc_id,
                    a.coarea,
                    c.nume as coarea_nume,
                    c.acronim as coarea_acronim,
                    a.hier,
                    trim(replace(replace(replace(a.descr,char(13),' '), char(10), ' '), '"','')) as descr,
                    a.data_set,
                    d.nume as data_set_nume,
                    d.vers as data_set_vers,
                    d.an as data_set_an,
                    a.cost_centre,
                    e.nume as cost_centre_nume,
                    e.blocat as cost_centre_blocat,
                    a.cheie,
                    m.nume as cheie_nume,
                    trim(replace(replace(replace(m.descr,char(13),' '), char(10), ' '), '"','')) as cheie_descr,
                    m.ktype as cheie_tip,
                    f.cost_driver,
                    g.nume as cost_driver_nume,
                    g.central as cost_driver_central,
                    a.opex_categ,
                    f.nume as opex_categ_nume,
                    a.ic_part,
                    h.nume as ic_part_nume,
                    b.id as val_id,
                    'planificat' as val_tip,
                    k.actual,
                    coalesce(b.cont, f.cont_ccoa) as cont,
                    null as buss_line,
                    null as buss_line_seg,
                    null as buss_line_nume,
                    b.an,
                    b.per,
                    b.valoare,
                    a.mod_de as doc_mod_de,
                    a.mod_timp as doc_mod_timp,
                    b.mod_de as val_mod_de,
                    b.mod_timp as val_mod_timp
                from oxpl.tbl_int_recs_plan_head as a

                inner join oxpl.tbl_int_recs_plan_vals as b
                on a.id = b.head_id

                inner join oxpl.tbl_int_coarea as c
                on a.coarea = c.cod

                inner join oxpl.tbl_int_data_set as d
                on a.data_set = d.id

                inner join oxpl.tbl_int_ccntr as e
                on a.data_set = e.data_set and a.hier = e.hier and a.cost_centre = e.cod

                inner join oxpl.tbl_int_opex_categ as f
                on a.opex_categ = f.cod

                inner join oxpl.tbl_int_cost_driver as g
                on f.cost_driver = g.cod

                left join oxpl.tbl_int_ic_part as h
                on a.ic_part = h.cod
                
                inner join oxpl.tbl_int_data_set_per as k
                on b.data_set = k.data_set and b.an = k.an and b.per = k.per

                left join oxpl.tbl_int_key_head as m
                on a.cheie = m.id
                
                where a.data_set = @set and a.coarea = @coarea;

                if @has_alocare = 1 /* salvare calcul alocare in tabela de model pentru ariile de controlling cu alocare */
                    begin
                        /* calcul procente de alocare */
                        drop table if exists #plan_key_prc;

                        select
                            a.cheie,
                            a.cheie_nume,
                            a.cheie_descr,
                            a.cheie_tip,
                            a.cost_centre,
                            a.an,
                            b.buss_line,
                            round(b.valoare / a.total, 6) as procent
                        into #plan_key_prc
                        from (select
                                m.cheie,
                                m.cheie_nume,
                                m.cheie_descr,
                                m.cheie_tip,
                                n.gen_data_set,
                                m.cost_centre,
                                n.an,
                                sum(n.valoare) as total
                            from (select distinct
                                    a.cheie,
                                    b.nume as cheie_nume,
                                    b.descr as cheie_descr,
                                    b.ktype as cheie_tip,
                                    (case when b.ktype in ('G01', 'S01') then a.cost_centre else null end) as cost_centre
                                from oxpl.tbl_int_recs_plan_head as a
                                inner join oxpl.tbl_int_key_head as b
                                on a.cheie = b.id
                                where a.data_set = @set and a.coarea = @coarea) as m

                            inner join oxpl.tbl_int_key_vals as n
                            on m.cheie = n.cheie and (m.cost_centre = n.cost_centre or (m.cost_centre is null and n.cost_centre is null))
                            
                            where n.gen_data_set = @set
                            group by m.cheie, m.cheie_nume, m.cheie_descr, m.cheie_tip, n.gen_data_set, m.cost_centre, n.an) as a

                        inner join oxpl.tbl_int_key_vals as b
                        on a.cheie = b.cheie and a.gen_data_set = b.gen_data_set and
                            (a.cost_centre = b.cost_centre or (a.cost_centre is null and b.cost_centre is null)) and
                            a.an = b.an

                        where a.total != 0;

                        /* efectuare calcul si salvare */
                        insert into oxpl.tbl_mdl_alocare(doc_id, coarea, coarea_nume, coarea_acronim, hier, descr, data_set, data_set_nume, data_set_vers, data_set_an,
                                                cost_centre, cost_centre_nume, cost_centre_blocat, cheie, cheie_nume, cheie_descr, cheie_tip, cost_driver, cost_driver_nume, cost_driver_central,
                                                opex_categ, opex_categ_nume, ic_part, ic_part_nume, val_id, val_tip, actual, cont, buss_line, buss_line_seg, buss_line_nume,
                                                an, per, valoare, doc_mod_de, doc_mod_timp, val_mod_de, val_mod_timp)
                        select
                            a.id as doc_id,
                            a.coarea,
                            c.nume as coarea_nume,
                            c.acronim as coarea_acronim,
                            a.hier,
                            trim(replace(replace(replace(a.descr,char(13),' '), char(10), ' '), '"','')) as descr,
                            a.data_set,
                            d.nume as data_set_nume,
                            d.vers as data_set_vers,
                            d.an as data_set_an,
                            a.cost_centre,
                            e.nume as cost_centre_nume,
                            e.blocat as cost_centre_blocat,
                            a.cheie,
                            m.cheie_nume,
                            trim(replace(replace(replace(m.cheie_descr,char(13),' '), char(10), ' '), '"','')) as cheie_descr,
                            m.cheie_tip,
                            f.cost_driver,
                            g.nume as cost_driver_nume,
                            g.central as cost_driver_central,
                            a.opex_categ,
                            f.nume as opex_categ_nume,
                            a.ic_part,
                            h.nume as ic_part_nume,
                            b.id as val_id,
                            'alocat' as val_tip,
                            k.actual,
                            coalesce(b.cont, f.cont_ccoa) as cont,
                            m.buss_line,
                            n.seg_ind as buss_line_seg,
                            n.nume as buss_line_nume,
                            b.an,
                            b.per,
                            (b.valoare * m.procent) as valoare,
                            a.mod_de as doc_mod_de,
                            a.mod_timp as doc_mod_timp,
                            b.mod_de as val_mod_de,
                            b.mod_timp as val_mod_timp
                        from oxpl.tbl_int_recs_plan_head as a

                        inner join oxpl.tbl_int_recs_plan_vals as b
                        on a.id = b.head_id

                        inner join oxpl.tbl_int_coarea as c
                        on a.coarea = c.cod

                        inner join oxpl.tbl_int_data_set as d
                        on a.data_set = d.id

                        inner join oxpl.tbl_int_ccntr as e
                        on a.data_set = e.data_set and a.hier = e.hier and a.cost_centre = e.cod

                        inner join oxpl.tbl_int_opex_categ as f
                        on a.opex_categ = f.cod

                        inner join oxpl.tbl_int_cost_driver as g
                        on f.cost_driver = g.cod

                        left join oxpl.tbl_int_ic_part as h
                        on a.ic_part = h.cod

                        inner join oxpl.tbl_int_data_set_per as k
                        on b.data_set = k.data_set and b.an = k.an and b.per = k.per

                        inner join #plan_key_prc as m
                        on a.cheie = m.cheie and b.an = m.an and (a.cost_centre = m.cost_centre or m.cost_centre is null)

                        inner join oxpl.tbl_int_buss_line as n
                        on m.buss_line = n.cod

                        where a.data_set = @set and a.coarea = @coarea;
                    end;
            commit transaction
        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            throw;
        end catch
    end;