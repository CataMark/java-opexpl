create or alter procedure oxpl.prc_cheie_s01_insert_return
    @json nvarchar(max),
    @kid varchar(20)
as
    begin
        set nocount on;
        begin try
            begin transaction
                declare @id table (
                    cheie int,
                    coarea char(4),
                    data_set int,
                    hier char(5),
                    cost_centre varchar(10)
                );

                /* introducere cheie */
                insert into oxpl.tbl_int_key_head (nume, descr, coarea, ktype, blocat, data_set, hier, cost_centre, mod_de, mod_timp)
                output inserted.id, inserted.coarea, inserted.data_set, inserted.hier, inserted.cost_centre
                into @id(cheie, coarea, data_set, hier, cost_centre)

                select top 1 a.nume, a.descr, a.coarea, a.ktype, a.blocat, a.data_set, a.hier, a.cost_centre, @kid, current_timestamp
                from openjson(@json)
                    with(
                        nume nvarchar(50) '$.nume',
                        descr nvarchar(4000) '$.descr',
                        coarea char(4) '$.coarea',
                        ktype char(3) '$.ktype',
                        blocat bit '$.blocat',
                        data_set int '$.data_set',
                        hier char(5) '$.hier',
                        cost_centre varchar(10) '$.cost_centre'
                    ) as a;

                /* introducere valori cheie */
                insert into oxpl.tbl_int_key_vals (cheie, coarea, buss_line, hier, data_set, cost_centre, gen_data_set, an, valoare, mod_de, mod_timp)
                select b.cheie, b.coarea, a.buss_line, b.hier, b.data_set, b.cost_centre, b.data_set as gen_data_set, a.an, a.valoare, @kid, current_timestamp
                from openjson(json_query(@json, '$.valori'))
                    with(
                        buss_line char(4) '$.buss_line',
                        an smallint '$.an',
                        valoare float '$.valoare'
                    ) as a
                cross apply @id as b
                where a.valoare != 0 or a.valoare is not null;;

                /* obtine raspuns */
                select b.*
                from @id as a
                cross apply (select * from oxpl.fnc_cheie_s01_get_by_id_aggr(a.cheie)) as b;
            commit transaction
        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            throw;
        end catch
    end;