create or alter procedure oxpl.prc_cheie_g01_val_maintain
    @cheie int,
    @set int,
    @cost_centre varchar(10),
    @json nvarchar(max),
    @kid varchar(20)
as
    begin
        set nocount on;

        exec sys.sp_set_session_context @key = N'oxpl_user_id', @value = @kid, @readonly = 0;

        begin try
            begin transaction
                /* actualizare valori cheie */
                merge into oxpl.tbl_int_key_vals as t
                using (select a.*
                        from openjson(json_query(@json, '$.valori'))
                            with(
                                id uniqueidentifier '$.id',
                                cheie int '$.cheie',
                                coarea char(4) '$.coarea',
                                buss_line char(4) '$.buss_line',
                                hier char(5) '$.hier',
                                data_set int '$.data_set',
                                cost_centre varchar(10) '$.cost_centre',
                                gen_data_set int '$.gen_data_set',
                                an smallint '$.an',
                                valoare float '$.valoare'
                            ) as a
                        where a.valoare is not null and a.valoare !=0) as s
                on (t.id = s.id)
                when matched then
                    update set
                        t.buss_line = s.buss_line,
                        t.hier = s.hier,
                        t.data_set = s.data_set,
                        t.cost_centre = s.cost_centre,
                        t.an = s.an,
                        t.valoare = s.valoare,
                        t.mod_de = @kid,
                        t.mod_timp = current_timestamp
                when not matched by target then
                    insert (cheie, coarea, buss_line, hier, data_set, cost_centre, gen_data_set, an, valoare, mod_de, mod_timp)
                    values (s.cheie, s.coarea, s.buss_line, s.hier, s.data_set, s.cost_centre, s.gen_data_set, s.an, s.valoare, @kid, current_timestamp)
                when not matched by source and t.cheie = @cheie and t.gen_data_set = @set and t.cost_centre = @cost_centre then
                    delete;
            commit transaction

            /* obtinere valori salvate pentru cheie si centru de cost */
            select
                a.id,
                a.hier,
                a.data_set,
                a.cod,
                a.nume,
                a.grup,
                a.blocat,
                cast(1 as bit) as leaf,
                cast((select distinct
                        m.an,
                        sum(m.valoare) over (partition by m.an) as valoare,
                        first_value(m.mod_de) over (partition by m.cost_centre order by m.mod_timp desc rows between unbounded preceding and unbounded following) as mod_de,
                        max(m.mod_timp) over (partition by m.cost_centre) as mod_timp
                        from oxpl.tbl_int_key_vals as m
                        where m.cheie = @cheie and m.gen_data_set = @set and m.hier = a.hier and m.cost_centre = a.cod
                        order by m.an asc
                        for json path) as nvarchar(max)) as valori
            from oxpl.tbl_int_ccntr as a
            where a.data_set = @set and a.hier = (select top 1 n.cc_hier
                                                    from oxpl.tbl_int_key_head as m
                                                    inner join oxpl.tbl_int_coarea as n
                                                    on m.coarea = n.cod
                                                    where m.id = @cheie)
                and a.cod = @cost_centre;
        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            throw;
        end catch
    end;