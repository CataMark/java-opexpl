create or alter procedure oxpl.prc_cheie_c01_calculate
    @set int,
    @coarea char(4),
    @cheie int = null,
    @kid varchar(20)
as
    begin
        set nocount on;

        exec sys.sp_set_session_context @key = N'oxpl_user_id', @value = @kid, @readonly = 0;

        begin try
            /* verificare existenta reguli de calcul */
            if not exists (select *
                            from oxpl.tbl_int_key_rule as a
                            inner join oxpl.tbl_int_key_head as b
                            on a.cheie = b.id
                            where b.ktype = 'C01' and b.coarea = @coarea and
                                1 = (case when @cheie is null then 1 else iif(b.id = @cheie, 1, 0) end) and
                                1 = (case when @cheie is not null then 1 else iif(b.blocat = 0, 1, 0) end))
                raiserror('Nu există reguli de calcul pentru cheia/ cheile în scop!', 16, 1);

            /* tabela reguli de calcul */
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
            where a.ktype = 'C01' and a.coarea = @coarea and
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

            /* tabela valori cheie de calcul */
            drop table if exists #oxpl_key_vals;
            create table #oxpl_key_vals(
                cheie_tinta int not null,
                coarea char(4) not null,
                buss_line char(4) not null,
                data_set int not null,
                an smallint not null,
                valoare float not null
            );

            /* valori chei de calcul cu medie aritmetica */
            insert into #oxpl_key_vals(cheie_tinta, coarea, buss_line, data_set, an, valoare)
            select
                a.cheie_tinta,
                a.coarea,
                b.buss_line,
                a.data_set,
                b.an,
                round(sum(b.valoare), 2) as valoare
            from #oxpl_key_criterii as a

            inner join oxpl.tbl_int_key_vals as b
            on a.cheie_sursa = b.cheie and a.data_set = b.gen_data_set and a.coarea = b.coarea

            where a.medie_pond = 0 and
                1 = (case when a.cost_centre is null or b.cost_centre is null then 1 else iif(a.cost_centre = b.cost_centre, 1, 0) end)
            group by a.cheie_tinta, a.coarea, b.buss_line, a.data_set, b.an;

            /* inregistrari chei de calcul cu medie ponderata */
            drop table if exists #oxpl_key_recs;
            create table #oxpl_key_recs(
                cheie_tinta int not null,
                cheie_sursa int not null,
                coarea char(4) not null,
                data_set int not null,
                cost_centre varchar(10) not null,
                an smallint not null,
                valoare float not null
            );

            insert into #oxpl_key_recs(cheie_tinta, cheie_sursa, coarea, data_set, cost_centre, an, valoare)
            select
                a.cheie_tinta,
                b.cheie as cheie_sursa,
                a.coarea,
                a.data_set,
                b.cost_centre,
                c.an,
                sum(c.valoare) as valoare
            from #oxpl_key_criterii as a

            inner join oxpl.tbl_int_recs_plan_head as b
            on a.data_set = b.data_set and a.coarea = b.coarea

            inner join oxpl.tbl_int_recs_plan_vals as c
            on b.id = c.head_id

            inner join oxpl.tbl_int_opex_categ as d
            on b.opex_categ = d.cod

            where a.medie_pond = 1 and b.cheie = a.cheie_sursa and
                1 = (case when a.cost_centre is null then 1 else iif(b.cost_centre = a.cost_centre, 1, 0) end) and
                1 = (case when a.cost_driver is null then 1 else iif(d.cost_driver = a.cost_driver, 1, 0) end) and
                1 = (case when a.opex_categ is null then 1 else iif(b.opex_categ = a.opex_categ, 1, 0) end) and
                1 = (case
                        when a.ic_part_flag = 0 and a.ic_part is null then 1
                        when a.ic_part_flag = 1 and a.ic_part is null and b.ic_part is null then 1
                        else iif(b.ic_part = a.ic_part, 1, 0) end)
            group by a.cheie_tinta, b.cheie, a.coarea, a.data_set, b.cost_centre, c.an;

            /* valori alocate cheie de calcul cu medie ponderata */
            with key_val as (
                select
                    a.cheie,
                    a.coarea,
                    a.gen_data_set,
                    a.cost_centre,
                    a.an,
                    sum(a.valoare) as valoare
                from oxpl.tbl_int_key_vals as a

                inner join (select distinct
                                m.cheie_sursa,
                                m.coarea,
                                m.data_set,
                                (case n.ktype when 'G02' then cast(null as varchar(10)) else m.cost_centre end) as cost_centre
                            from #oxpl_key_recs as m
                            inner join oxpl.tbl_int_key_head as n
                            on m.cheie_sursa = n.id) as b
                on a.cheie = b.cheie_sursa and a.coarea = b.coarea and a.gen_data_set = b.data_set and
                    ((a.cost_centre is null and b.cost_centre is null) or a.cost_centre = b.cost_centre)
                group by a.cheie, a.coarea, a.gen_data_set, a.cost_centre, a.an
                
            ),
            key_prc as (
                select
                    a.cheie,
                    a.cost_centre,
                    a.an,
                    b.buss_line,
                    (b.valoare / a.valoare) as procent
                from key_val as a
                inner join oxpl.tbl_int_key_vals as b
                on a.cheie = b.cheie and a.coarea = b.coarea and a.gen_data_set = b.gen_data_set and
                    ((a.cost_centre is null and b.cost_centre is null) or (a.cost_centre = b.cost_centre))
            )
            insert into #oxpl_key_vals(cheie_tinta, coarea, buss_line, data_set, an, valoare)
            select
                a.cheie_tinta,
                a.coarea,
                b.buss_line,
                a.data_set,
                a.an,
                round(sum(a.valoare * b.procent), 2) as valoare
            from #oxpl_key_recs as a
            inner join key_prc as b
            on a.cheie_sursa = b.cheie and a.an = b.an
            where 1 = (case when b.cost_centre is null then 1 else iif(a.cost_centre = b.cost_centre, 1, 0) end)
            group by a.cheie_tinta, a.coarea, b.buss_line, a.data_set, a.an;

            begin transaction
                /* sterge valori vechi pentru cheile ce nu sunt blocate */
                delete a
                from oxpl.tbl_int_key_vals as a
                inner join (select distinct cheie_tinta, data_set, coarea from #oxpl_key_criterii) as b
                on a.cheie = b.cheie_tinta and a.gen_data_set = b.data_set and a.coarea = b.coarea;

                /* introducere valori noi */
                with sums as (
                    select
                        a.cheie_tinta,
                        a.coarea,
                        a.data_set,
                        a.an,
                        sum(a.valoare) as valoare
                    from #oxpl_key_vals as a
                    group by a.cheie_tinta, a.coarea, a.data_set, a.an
                )
                insert into oxpl.tbl_int_key_vals (cheie, coarea, buss_line, gen_data_set, an, valoare, mod_de, mod_timp)
                select
                    a.cheie_tinta,
                    a.coarea,
                    b.buss_line,
                    a.data_set,
                    a.an,
                    (b.valoare / a.valoare) as valoare,
                    @kid,
                    current_timestamp
                from sums as a
                cross apply (select
                                m.buss_line,
                                sum(m.valoare) as valoare
                            from #oxpl_key_vals as m
                            where m.cheie_tinta = a.cheie_tinta and m.an = a.an
                            group by m.buss_line) as b

            commit transaction

            if @cheie is not null
                select
                    a.*,
                    cast((select distinct
                        n.an,
                        sum(n.valoare) over (partition by n.an) as valoare,
                        first_value(n.mod_de) over (partition by n.cheie order by n.mod_timp desc) as mod_de,
                        max(n.mod_timp) over (partition by n.cheie) as mod_timp
                    from oxpl.tbl_int_key_vals as n
                    where n.cheie = a.id and n.gen_data_set = @set
                    order by n.an asc
                    for json path) as nvarchar(max)) as valori
                from oxpl.tbl_int_key_head as a
                where a.id = @cheie;
        end try
        begin catch
            if @@trancount > 0  rollback transaction;
            throw;
        end catch
    end;