create or alter procedure oxpl.prc_costcentermap_get_list_not_mapped
    @hier char(5),
    @set int
as
    begin
        set nocount on;

        drop table if exists #oxpl_ccntr_sender;
        create table #oxpl_ccntr_sender(
            cod varchar(10) not null,
            nume nvarchar(100),
            val_compar_set float,
            val_actual_set float
        );

        /* colectare centrele de cost nemapate din seturile de date de referinta */
        insert into #oxpl_ccntr_sender(cod)
        select x.cod
        from (select a.cod
                from oxpl.tbl_int_ccntr as a
                inner join oxpl.tbl_int_data_set as b
                on a.data_set = b.impl_compare
                where b.id = @set and a.hier = @hier

                union

                select distinct a.cost_cntr as cod
                from oxpl.tbl_int_recs_act as a

                inner join oxpl.tbl_int_data_set as b
                on a.data_set = b.actual_set

                inner join oxpl.tbl_int_data_set_per as c
                on b.id = c.data_set and a.an = c.an and a.per = c.per

                inner join oxpl.tbl_int_coarea as d
                on a.coarea = d.cod

                where b.id = @set and d.cc_hier = @hier and c.actual = 1) as x
        except
        select x.cod
        from (select a.cod
                from oxpl.tbl_int_ccntr as a
                left join oxpl.tbl_int_ccntr_map as b
                on a.data_set = b.data_set and a.hier = b.hier and a.cod = b.sender
                where a.data_set = @set and a.hier = @hier and b.sender is null

                union

                select a.sender as cod
                from oxpl.tbl_int_ccntr_map  as a
                where a.data_set = @set and a.hier = @hier) as x;

        /* aducere nume centru de cost */
        update a
        set a.nume = b.nume
        from #oxpl_ccntr_sender as a
        inner join (select distinct
                        r.cod,
                        first_value(r.nume) over (partition by r.cod order by r.last_update desc rows between unbounded preceding and unbounded following) as nume
                    from (select
                            a.cod,
                            a.nume,
                            a.mod_timp as last_update
                        from oxpl.tbl_int_ccntr as a

                        inner join oxpl.tbl_int_data_set as b
                        on a.data_set = b.impl_compare

                        inner join #oxpl_ccntr_sender as c
                        on a.cod = c.cod

                        where b.id = @set and a.hier = @hier

                        union all
                        
                        select distinct
                            a.cost_cntr as cod,
                            first_value(a.cost_cntr_nume) over (partition by a.cost_cntr order by a.data_creat desc rows between unbounded preceding and unbounded following) as nume,
                            cast((max(a.data_creat) over (partition by a.cost_cntr)) as datetime) as last_update
                        from oxpl.tbl_int_recs_act as a

                        inner join oxpl.tbl_int_data_set as b
                        on a.data_set = b.actual_set

                        inner join oxpl.tbl_int_data_set_per as c
                        on b.id = c.data_set and a.an = c.an and a.per = c.per

                        inner join oxpl.tbl_int_coarea as d
                        on a.coarea = d.cod

                        inner join #oxpl_ccntr_sender as e
                        on a.cost_cntr = e.cod

                        where b.id = @set and d.cc_hier = @hier and c.actual = 1) as r) as b
        on a.cod = b.cod;

        /* introducere valori centre de cost */
        update a
            set a.val_compar_set = coalesce(b.valoare, 0),
                a.val_actual_set = coalesce(c.valoare, 0)
        from #oxpl_ccntr_sender as a

        left join (select
                        a.cost_centre as cod,
                        sum(b.valoare) as valoare
                    from oxpl.tbl_int_recs_plan_head as a

                    inner join oxpl.tbl_int_recs_plan_vals as b
                    on a.id = b.head_id

                    inner join  oxpl.tbl_int_data_set as c
                    on a.data_set = c.impl_compare
                    
                    inner join #oxpl_ccntr_sender as d
                    on a.cost_centre = d.cod
                    
                    where c.id = @set and a.hier = @hier                
                    group by a.cost_centre) as b
        on a.cod = b.cod

        left join (select
                        a.cost_cntr as cod,
                        sum(a.valoare) as valoare
                    from oxpl.tbl_int_recs_act as a
                    
                    inner join oxpl.tbl_int_data_set as b
                    on a.data_set = b.actual_set
                    
                    inner join oxpl.tbl_int_data_set_per as c
                    on b.id = c.data_set and a.an = c.an and a.per = c.per
                    
                    inner join oxpl.tbl_int_coarea as d
                    on a.coarea = d.cod
                    
                    inner join #oxpl_ccntr_sender as e
                    on a.cost_cntr = e.cod
                    
                    where b.id = @set and d.cc_hier = @hier and c.actual = 1
                    group by a.cost_cntr) as c
        on a.cod = c.cod;

        /* rezultat */
        select
            @hier as hier,
            @set as data_set,
            cod as sender,
            nume as sender_nume,
            val_compar_set,
            val_actual_set
        from #oxpl_ccntr_sender order by cod asc;
    end;