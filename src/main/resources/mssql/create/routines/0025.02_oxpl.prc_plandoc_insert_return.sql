create or alter procedure oxpl.prc_plandoc_insert_return
    @json nvarchar(max),
    @kid varchar(20)
as
    begin
        set nocount on;
        begin try
            begin transaction
                declare @id table(id uniqueidentifier not null, data_set int not null);

                /* introducere document */
                insert into oxpl.tbl_int_recs_plan_head (coarea, descr, hier, data_set, cost_centre, cheie, opex_categ, ic_part, mod_de, mod_timp)
                output inserted.id, inserted.data_set into @id(id, data_set)

                select top 1 a.coarea, a.descr, a.hier, a.data_set, a.cost_centre, a.cheie, a.opex_categ, a.ic_part, @kid, current_timestamp
                from openjson(@json) with (
                    coarea char(4) '$.coarea',
                    descr nvarchar(2000) '$.descr',
                    hier char(5) '$.hier',
                    data_set int '$.data_set',
                    cost_centre varchar(10) '$.cost_centre',
                    cheie int '$.cheie',
                    opex_categ int '$.opex_categ',
                    ic_part varchar(5) '$.ic_part'
                ) as a;

                /* introducere valori document */
                insert into oxpl.tbl_int_recs_plan_vals (head_id, cont, data_set, an, per, valoare, mod_de, mod_timp)
                select b.id, null, b.data_set, a.an, a.per, a.valoare, @kid, current_timestamp
                from openjson(json_query(@json, '$.valori')) with (
                    an smallint '$.an',
                    per char(2) '$.per',
                    valoare float '$.valoare'
                ) as a
                cross apply @id as b
                inner join oxpl.tbl_int_data_set_per as c
                on b.data_set = c.data_set and a.an = c.an and a.per = c.per
                where c.actual = 0  and a.valoare != 0;

                /* obtine raspuns */
                select b.*
                from @id as a
                cross apply (select * from oxpl.fnc_plandoc_get_by_id(a.id)) as b;
            commit transaction
        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            throw;
        end catch
    end;