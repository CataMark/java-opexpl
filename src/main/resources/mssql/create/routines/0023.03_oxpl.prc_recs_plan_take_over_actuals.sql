create or alter procedure oxpl.prc_recs_plan_take_over_actuals
    @hier char(5),
    @set int,
    @kid varchar(20)
as
    begin
        set nocount on;
        begin try
            begin transaction
                if exists (select * from oxpl.tbl_int_recs_plan_head as a
                            where a.hier = @hier and a.data_set = @set)
                    raiserror('Există deja documente pe setul de date şi aria de controlling selectate! Preluarea valorilor de realizat nu este posibilă!', 16, 1);

                drop table if exists #oxpl_take_over_actual;
                create table #oxpl_take_over_actual(
                    id int,
                    coarea char(4) not null,
                    descr nvarchar(100) not null,
                    hier char(5) not null,
                    data_set int not null,
                    cost_centre varchar(10) not null,
                    opex_categ int not null,
                    ic_part varchar(5),
                    cont char(10) not null,
                    an smallint not null,
                    per char(2) not null,
                    valoare float not null
                );

                /* aggregare valori de actual */
                insert into #oxpl_take_over_actual(coarea, descr, hier, data_set, cost_centre, opex_categ, ic_part, cont, an, per, valoare)
                select
                    a.coarea,
                    '(actual)' as descr,
                    b.cc_hier as hier,
                    c.id as data_set,
                    e.receiver as cost_centre,
                    a.opex_categ,
                    a.part_ic as ic_part,
                    a.cont_ccoa as cont,
                    a.an,
                    a.per,
                    round(sum(a.valoare), 4) as valoare
                from oxpl.tbl_int_recs_act as a

                inner join oxpl.tbl_int_coarea as b
                on a.coarea = b.cod

                inner join oxpl.tbl_int_data_set as c
                on a.data_set = c.actual_set

                inner join oxpl.tbl_int_data_set_per as d
                on d.data_set = c.id and a.an = d.an and a.per = d.per

                inner join oxpl.fnc_ccenter_map(@hier, @set) as e
                on a.cost_cntr = e.sender
                
                where b.cc_hier = @hier and c.id = @set and d.actual = 1
                group by a.coarea, b.cc_hier, c.id, e.receiver, a.opex_categ, a.part_ic, a.cont_ccoa, a.an, a.per
                order by e.receiver, a.opex_categ, a.cont_ccoa, a.part_ic, a.an, a.per;

                /* sterge valori zero */
                delete from #oxpl_take_over_actual where valoare = 0;

                /* verificare existenta valori realizate */
                if not exists(select * from #oxpl_take_over_actual)
                    raiserror('Nu există înregistrări realizate care după agregare şi mapare să aibă valori diferite de zero!', 16, 1);

                /* stabileste un id unic pentru fiecare document */
                update a
                set a.id = cast(b.id as int)
                from #oxpl_take_over_actual as a
                inner join
                    (select
                        row_number() over (order by m.cost_centre asc, m.opex_categ asc, m.ic_part asc) as id,
                        m.*
                    from (select distinct
                            cost_centre,
                            opex_categ,
                            ic_part
                        from #oxpl_take_over_actual) as m) as b
                on a.cost_centre = b.cost_centre and a.opex_categ = b.opex_categ and ((a.ic_part = b.ic_part) or (a.ic_part is null and b.ic_part is null));

                /* intoducere documente */
                drop table if exists #oxpl_take_over_actual_id;
                create table #oxpl_take_over_actual_id(
                    nid uniqueidentifier not null,
                    vid int not null
                );

                insert into oxpl.tbl_int_recs_plan_head ([uid], coarea, descr, hier, data_set, cost_centre, opex_categ, ic_part, mod_de, mod_timp)
                output inserted.id, inserted.[uid] into #oxpl_take_over_actual_id(nid, vid)
                select distinct
                    a.id,
                    a.coarea,
                    a.descr,
                    a.hier,
                    a.data_set,
                    a.cost_centre,
                    a.opex_categ,
                    a.ic_part,
                    @kid as mod_de,
                    current_timestamp as mod_timp
                from #oxpl_take_over_actual as a;

                /* introducere valori */
                insert into oxpl.tbl_int_recs_plan_vals (head_id, data_set, cont, an, per, valoare, mod_de, mod_timp)
                select
                    b.nid as head_id,
                    a.data_set,
                    a.cont,
                    a.an,
                    a.per,
                    a.valoare,
                    @kid as mod_de,
                    current_timestamp as mod_timp
                from #oxpl_take_over_actual as a
                inner join #oxpl_take_over_actual_id as b
                on a.id = b.vid;

                /* curatare uid */
                exec sys.sp_set_session_context @key = N'oxpl_not_backup', @value = 1, @readonly = 0;

                update a
                set a.[uid] = null
                from oxpl.tbl_int_recs_plan_head as a
                inner join #oxpl_take_over_actual_id as b
                on a.id = b.nid;

                /* verificare daca exista diferente */
                declare @sum_init float = 0;
                declare @sum_final float = 0;

                select @sum_init = coalesce(sum(a.valoare), 0)
                from oxpl.tbl_int_recs_act as a

                inner join oxpl.tbl_int_coarea as b
                on a.coarea = b.cod

                inner join oxpl.tbl_int_data_set as c
                on a.data_set = c.actual_set

                inner join oxpl.tbl_int_data_set_per as d
                on d.data_set = c.id and a.an = d.an and a.per = d.per

                where b.cc_hier = @hier and c.id = @set and d.actual = 1;

                select @sum_final = coalesce(sum(b.valoare), 0)
                from oxpl.tbl_int_recs_plan_head as a
                inner join oxpl.tbl_int_recs_plan_vals as b
                on a.id = b.head_id
                where a.hier = @hier and a.data_set = @set;

                select round(@sum_final - @sum_init, 0) as diferenta;
            commit transaction
        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            throw;
        end catch
    end;