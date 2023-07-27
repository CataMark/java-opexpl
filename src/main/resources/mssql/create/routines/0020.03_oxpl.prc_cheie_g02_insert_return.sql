create or alter procedure oxpl.prc_cheie_g02_insert_return
    @set int,
    @json nvarchar(max),
    @kid varchar(20)
as
    begin
        set nocount on;

        begin try
            begin transaction
                declare @id table(
                    cheie int,
                    coarea char(4)
                );

                /* introducere cheie */
                insert into oxpl.tbl_int_key_head (nume, descr, coarea, ktype, blocat, mod_de, mod_timp)
                output inserted.id, inserted.coarea into @id(cheie, coarea)
                
                select top 1 a.nume, a.descr, a.coarea, a.ktype, a.blocat, @kid, current_timestamp
                from openjson(@json)
                    with(
                        nume nvarchar(50) '$.nume',
                        descr nvarchar(4000) '$.descr',
                        coarea char(4) '$.coarea',
                        ktype char(3) '$.ktype',
                        blocat bit '$.blocat'
                    ) as a;

                /* introducere valori cheie */
                insert into oxpl.tbl_int_key_vals (cheie, coarea, buss_line, gen_data_set, an, valoare, mod_de, mod_timp)
                select b.cheie, b.coarea, a.buss_line, a.gen_data_set, a.an, a.valoare, @kid, current_timestamp
                from openjson(json_query(@json, '$.valori'))
                    with(
                        buss_line char(4) '$.buss_line',
                        gen_data_set int '$.gen_data_set',
                        an smallint '$.an',
                        valoare float '$.valoare'
                    ) as a
                cross apply @id as b
                where a.valoare != 0;

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