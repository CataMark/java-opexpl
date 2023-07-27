create or alter procedure oxpl.prc_cheie_g02_update_return
    @set int,
    @json nvarchar(max),
    @kid varchar(20)
as
    begin
        set nocount on;
        exec sys.sp_set_session_context @key = N'oxpl_user_id', @value = @kid, @readonly = 0;

        begin try
            begin transaction
                declare @id table(
                    cheie int,
                    coarea char(4)
                );

                /* actualizare campuri cheie */
                update a
                set a.nume = b.nume, a.descr = b.descr, a.blocat = b.blocat, a.mod_de = @kid, a.mod_timp = current_timestamp
                output inserted.id, inserted.coarea into @id(cheie, coarea)
                from oxpl.tbl_int_key_head as a
                inner join (select top 1 * from openjson(@json)
                                with(
                                    id int '$.id',
                                    nume nvarchar(50) '$.nume',
                                    descr nvarchar(4000) '$.descr',
                                    blocat bit '$.blocat'
                                )) as b
                on a.id = b.id;

                /* actualizare valori cheie */
                declare @cheie int;
                select top 1 @cheie = a.cheie from @id as a;

                merge into oxpl.tbl_int_key_vals as t
                using (select a.*, b.*
                        from openjson(json_query(@json, '$.valori'))
                            with(
                                id uniqueidentifier '$.id',
                                buss_line char(4) '$.buss_line',
                                gen_data_set int '$.gen_data_set',
                                an smallint '$.an',
                                valoare float '$.valoare'
                            ) as a
                        cross apply @id as b
                        where a.valoare is not null and a.valoare != 0) as s
                on (t.id = s.id)
                when matched then
                    update set
                        t.buss_line = s.buss_line,
                        t.gen_data_set = s.gen_data_set,
                        t.an = s.an,
                        t.valoare = s.valoare,
                        t.mod_de = @kid,
                        t.mod_timp = current_timestamp
                when not matched by target then
                    insert (cheie, coarea, buss_line, gen_data_set, an, valoare, mod_de, mod_timp)
                    values (s.cheie, s.coarea, s.buss_line, s.gen_data_set, s.an, s.valoare, @kid, current_timestamp)
                when not matched by source and t.cheie = @cheie and t.gen_data_set = @set then
                    delete;

                /* obtine raspuns */
                select b.*
                from @id as a
                cross apply (select * from oxpl.fnc_cheie_g02_get_by_id_aggr(a.cheie, @set)) as b;
            commit transaction
        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            throw;
        end catch
    end;