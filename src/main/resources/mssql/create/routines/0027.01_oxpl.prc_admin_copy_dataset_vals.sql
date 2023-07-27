create or alter procedure oxpl.prc_admin_copy_dataset_vals
    @source_set int,
    @dest_set int,
    @coarea char(4),
    @kid varchar(20)
as
    begin
        set nocount on;

        /* setare variabile de sesiune pentru a nu face backup */
        /* *************************************************** */
        exec sys.sp_set_session_context @key = N'oxpl_not_backup', @value = 1, @readonly = 0;
        exec sys.sp_set_session_context @key = N'oxpl_user_id', @value = @kid, @readonly = 0;

        begin try
            if @source_set = @dest_set
                raiserror('Setul de date destinatar nu poate fi același cu cel sursă!', 16, 1);

            if exists (select * from oxpl.tbl_int_data_set as a
                        inner join oxpl.tbl_int_plan_vers as b
                        on a.vers = b.cod
                        where a.id = @source_set and b.actual = 1)
                raiserror('Setul de date sursă trebuie să fie de tip planificat!', 16, 1);

            if exists (select * from oxpl.tbl_int_data_set as a
                        inner join oxpl.tbl_int_plan_vers as b
                        on a.vers = b.cod
                        where a.id = @dest_set and (b.actual = 1 or a.incheiat = 1))
                raiserror('Setul de date destinatar trebuie să fie de tip planificat și nu trebuie să fie încheiat!', 16, 1);

            if exists (select *
                        from (select * from oxpl.tbl_int_data_set_per as m where m.data_set = @source_set) as a
                        left join (select * from oxpl.tbl_int_data_set_per as n where n.data_set = @dest_set) as b
                        on a.an = b.an and a.per = b.per and a.actual = b.actual
                        where b.id is null)
                raiserror('Există perioade din setul de date sursă care nu se regăsesc sau nu au aceleași setări în setul destinație!', 16, 1);

            if exists (select *
                        from oxpl.tbl_int_ccntr_grup as a
                        inner join oxpl.tbl_int_coarea as b
                        on a.hier = b.cc_hier
                        where a.data_set = @dest_set and b.cod = @coarea)
                raiserror('Există deja grupuri de centre de cost în setul de date și aria de controlling destinatare!', 16, 1);

            if exists(select *
                        from oxpl.tbl_int_ccntr as a
                        inner join oxpl.tbl_int_coarea as b
                        on a.hier = b.cc_hier
                        where a.data_set = @dest_set and b.cod = @coarea)
                raiserror('Există deja centre de cost în setul de date și aria de controlling destinatare!', 16, 1);

            if exists (select * from oxpl.tbl_int_key_head as a
                        where a.data_set is not null and a.data_set = @dest_set and a.coarea = @coarea)
                raiserror('Există deja chei de alocare specifice în setul de date și aria de controlling destinatare!', 16, 1);

            if exists (select * from oxpl.tbl_int_key_vals as a
                        where a.gen_data_set = @dest_set and a.coarea = @coarea)
                raiserror('Există deja valori pe chei de alocare în setul de date și aria de controlling destinatare!', 16, 1);

            if exists (select * from oxpl.tbl_int_recs_plan_head as a
                        where a.data_set = @dest_set and a.coarea = @coarea)
                raiserror('Există deja documente de planificare în setul de date și aria de controlling destinatare!', 16, 1);

            begin transaction

                /* migrare grupuri de centre de cost */
                /* ************************************ */

                /* pregatire ierarhie de grupuri de centre de cost */
                drop table if exists #ccntr_grup_hier;
                with cte as (
                    select a.*, cast(null as varchar(10)) as superior_cod from oxpl.tbl_int_ccntr_grup as a
                    inner join oxpl.tbl_int_coarea as b
                    on a.hier = b.cc_hier
                    where a.data_set = @source_set and b.cod = @coarea and a.superior is null

                    union all

                    select a.*, b.cod as superior_cod from oxpl.tbl_int_ccntr_grup as a
                    inner join cte as b
                    on a.superior = b.id)
                select * into #ccntr_grup_hier from cte;

                /* variabila ce va retine noul id al nodului de ierarhie */
                declare @nod_id table (id uniqueidentifier);

                /* copiere nod ierarhie in setul de date destinatar */
                insert into oxpl.tbl_int_ccntr_grup(hier, data_set, cod, nume, mod_de, mod_timp)
                output inserted.id into @nod_id(id)
                select a.hier, @dest_set, a.cod, a.nume, @kid, current_timestamp
                from #ccntr_grup_hier as a
                where a.superior is null;

                /* pregatire tabela ce va retine noile id-uri pentru codurile de grupuri de centre de cost */
                drop table if exists #ccntr_grup_match;
                create table #ccntr_grup_match (id uniqueidentifier not null, cod varchar(10) not null);
                
                /* copiere restul grupurilor in setul de date destinatar cu superior nodul de ierarhie */
                insert into oxpl.tbl_int_ccntr_grup(hier, data_set, cod, nume, superior, mod_de, mod_timp)
                output inserted.id, inserted.cod into #ccntr_grup_match(id, cod)
                select a.hier, @dest_set, a.cod, a.nume, b.id as superior, @kid, current_timestamp
                from #ccntr_grup_hier as a
                cross apply @nod_id as b
                where a.superior is not null;

                /* actualizare superior pentru grupurile de centre de cost */
                update a
                set a.superior = d.id
                from oxpl.tbl_int_ccntr_grup as a
                inner join oxpl.tbl_int_coarea as b on a.hier = b.cc_hier
                inner join #ccntr_grup_hier as c on a.cod = c.cod
                inner join #ccntr_grup_match as d on c.superior_cod = d.cod
                where a.data_set = @dest_set and b.cod = @coarea and a.superior is not null;

                /* curatare tabele folosite in migrare */
                drop table #ccntr_grup_hier;
                drop table #ccntr_grup_match;

                /* migrare centre de cost */
                /* ************************************ */
				insert into oxpl.tbl_int_ccntr(hier, data_set, cod, nume, grup, blocat, mod_de, mod_timp)
				select a.hier, @dest_set, a.cod, a.nume, a.grup, a.blocat, @kid, current_timestamp
				from oxpl.tbl_int_ccntr as a
				inner join oxpl.tbl_int_coarea as b
				on a.hier = b.cc_hier
				where a.data_set = @source_set and b.cod = @coarea;

                /* migrare header chei de alocare specifice setului de date */
                /* ************************************ */

				/* pregatire tabela ce va retine noul id al cheii de alocare */
				drop table if exists #key_spf_match;
				create table #key_spf_match (vid int not null, nid int not null);

				/* migrare header chei de alocare specifice */                
				merge into oxpl.tbl_int_key_head as t
				using (select *
						from oxpl.tbl_int_key_head as a
						where a.data_set = @source_set and a.coarea = @coarea) as s
				on (1 = 0)
				when not matched by target then
					insert (nume, descr, coarea, ktype, blocat, data_set, hier, cost_centre, mod_de, mod_timp)
					values (s.nume, s.descr, s.coarea, s.ktype, s.blocat, @dest_set, s.hier, s.cost_centre, @kid, current_timestamp)
					output s.id, inserted.id into #key_spf_match(vid, nid);

                /* migrare valori chei de alocare */
                /* ************************************ */
				insert into oxpl.tbl_int_key_vals(cheie, coarea, buss_line, hier, data_set, cost_centre, gen_data_set, an, valoare, mod_de, mod_timp)
				select
					coalesce(b.nid, a.cheie) as cheie,
					a.coarea,
					a.buss_line,
					a.hier,
					(case when a.data_set is null then a.data_set else @dest_set end) as data_set,
					a.cost_centre,
					@dest_set as gen_data_set,
					a.an,
					a.valoare,
					@kid,
					current_timestamp
				from oxpl.tbl_int_key_vals as a
				left join #key_spf_match as b
				on a.cheie = b.vid
				where a.coarea = @coarea and a.gen_data_set = @source_set;

                /* migrare header documente de planificare */
                /* ************************************ */

				/* pregatire tabela ce va retine noul id al documentului */
				drop table if exists #pdoc_match;
				create table #pdoc_match (vid uniqueidentifier not null, nid uniqueidentifier not null);

				/* migrare header documente */
				merge into oxpl.tbl_int_recs_plan_head as t
				using (select a.id, a.coarea, a.descr, a.hier, a.cost_centre, coalesce(b.nid, a.cheie) as cheie, a.opex_categ, a.ic_part
						from oxpl.tbl_int_recs_plan_head as a
						left join #key_spf_match as b
						on a.cheie = b.vid
						where a.coarea = @coarea and a.data_set = @source_set) as s
				on (1 = 0)
				when not matched by target then
					insert (coarea, descr, hier, data_set, cost_centre, cheie, opex_categ, ic_part, mod_de, mod_timp)
					values (s.coarea, s.descr, s.hier, @dest_set, s.cost_centre, s.cheie, s.opex_categ, s.ic_part, @kid, current_timestamp)
					output s.id, inserted.id into #pdoc_match(vid, nid);

                /* migrare valori documente de planificare */
                /* ************************************ */
				insert into oxpl.tbl_int_recs_plan_vals(head_id, cont, data_set, an, per, valoare, mod_de, mod_timp)
				select b.nid, a.cont, @dest_set, a.an, a.per, a.valoare, @kid, current_timestamp
				from oxpl.tbl_int_recs_plan_vals as a
				inner join #pdoc_match as b
				on a.head_id = b.vid
				where a.data_set = @source_set;

                /* curatare tabele temporare ramase */
                drop table #key_spf_match;
                drop table #pdoc_match;
            commit transaction
        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            throw;
        end catch
    end;