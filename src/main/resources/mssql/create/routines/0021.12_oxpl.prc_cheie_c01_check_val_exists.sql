create or alter procedure oxpl.prc_cheie_c01_check_val_exists
    @set int,
    @coarea char(4),
    @cheie int = null
as
    begin
        set nocount on;
        
        drop table if exists #oxpl_key_criterii;
        create table #oxpl_key_criterii(
            cheie_tinta int not null,
            data_set int not null,
            coarea char(4) not null,
            medie_pond bit not null,
            cheie_sursa int not null,
            cost_centre varchar(10),
            cost_driver char(5),
            opex_categ int,
            ic_part varchar(5),
            ic_part_flag bit not null default 0
        );

        /* introducere reguli de calcul */
        insert into #oxpl_key_criterii(cheie_tinta, data_set, coarea, medie_pond, cheie_sursa, cost_centre, cost_driver, opex_categ, ic_part)
        select
            a.id,
            @set,
            a.coarea,
            b.medie_pond,
            c.cheie_sursa,
            d.cost_centre,
            e.cost_driver,
            e.opex_categ,
            f.ic_part
        from oxpl.tbl_int_key_head as a

        inner join oxpl.tbl_int_key_rule as b
        on a.id = b.cheie

        outer apply (select cast(value as int) as cheie_sursa from openjson(b.chei_json)) as c
        outer apply (select cast(value as varchar(10)) as cost_centre from openjson(b.cost_centre_json)) as d
        outer apply (select cost_driver, opex_categ
                    from openjson(b.opex_categ_json)
                        with(
                            cost_driver char(5) '$.cost_driver',
                            opex_categ int '$.opex_categ'
                        )) as e
        outer apply (select cast(value as varchar(5)) as ic_part from openjson(b.ic_part_json)) as f
        where a.coarea = @coarea and
            1 = (case when @cheie is null then 1 else iif(a.id = @cheie, 1, 0) end) and
            1 = (case when @cheie is not null then 1 else iif(a.blocat = 0, 1, 0) end);

        /* actualizare flag pentru partener intercompanie */
        with flags as (
            select
                a.cheie_tinta,
                b.ic_part_flag
            from (select distinct cheie_tinta from #oxpl_key_criterii) as a
            cross apply (select (case when exists (select * from #oxpl_key_criterii as m where m.cheie_tinta = a.cheie_tinta and m.ic_part is not null) then 1
                                else 0 end) as ic_part_flag) as b
        )
        update a
        set a.ic_part_flag = b.ic_part_flag
        from #oxpl_key_criterii as a
        inner join flags as b
        on a.cheie_tinta = b.cheie_tinta;

        /* tabela rezultat cheie de calcul */
        drop table if exists #oxpl_key_has_values;
        create table #oxpl_key_has_values(
            cheie int not null,
            has_values bit not null
        );

        /* marcare chei care nu au regula de calcul */
        insert into #oxpl_key_has_values (cheie, has_values)
        select a.id as cheie, cast(0 as bit) as has_values
        from oxpl.tbl_int_key_head as a
        left join oxpl.tbl_int_key_rule as b
        on a.id = b.cheie
        where a.coarea = @coarea and a.ktype = 'C01' and a.blocat = 0 and b.cheie is null and
            1 = (case when @cheie is null then 1 else iif(a.id = @cheie, 1, 0) end) and
            1 = (case when @cheie is not null then 1 else iif(a.blocat = 0, 1, 0) end);

        /* verificare existenta valori chei sursa */
        with keys as (
            select distinct
                a.cheie_tinta
            from #oxpl_key_criterii as a
        )
        insert into #oxpl_key_has_values(cheie, has_values)
        select
            a.cheie_tinta,
            (case when exists (select *
                                from #oxpl_key_criterii as m
                                inner join oxpl.tbl_int_key_vals as n
                                on m.cheie_sursa = n.cheie and m.data_set = n.gen_data_set and m.coarea = n.coarea
                                where m.cheie_tinta = a.cheie_tinta and
                                    1 = (case when m.cost_centre is null or n.cost_centre is null then 1 else iif(m.cost_centre = n.cost_centre, 1, 0) end))
            then 1 else 0 end) as has_values
        from keys as a;

        /* verificare existenta inregistrari */
        with keys as (
            select distinct
                a.cheie_tinta
            from #oxpl_key_criterii as a
            where a.medie_pond = 1
        )
        update a
        set a.has_values = a.has_values & (case when exists (select * from oxpl.tbl_int_recs_plan_head as m

                                                            inner join oxpl.tbl_int_opex_categ as n
                                                            on m.opex_categ = n.cod

                                                            inner join #oxpl_key_criterii as q
                                                            on m.data_set = q.data_set and m.coarea = q.coarea

                                                            where q.cheie_tinta = a.cheie and
                                                                1 = (case when q.cost_centre is null then 1 else iif(m.cost_centre = q.cost_centre, 1, 0) end) and
                                                                1 = (case when q.cost_driver is null then 1 else iif(n.cost_driver = q.cost_driver, 1, 0) end) and
                                                                1 = (case when q.opex_categ is null then 1 else iif(m.opex_categ = q.opex_categ, 1, 0) end) and
                                                                1 = (case
                                                                        when q.ic_part_flag = 0 and q.ic_part is null then 1
                                                                        when q.ic_part_flag = 1 and q.ic_part is null and m.ic_part is null then 1
                                                                        else iif(m.ic_part = q.ic_part, 1, 0) end))
                                            then cast(1 as bit) else cast(0 as bit) end)
        from #oxpl_key_has_values as a
        inner join keys as b
        on a.cheie = b.cheie_tinta;

        /* rezultat: chei fara valori */
        select
            b.id,
            b.nume
        from #oxpl_key_has_values as a
        inner join oxpl.tbl_int_key_head as b
        on a.cheie = b.id
        where a.has_values = 0
        order by b.id asc;
    end;