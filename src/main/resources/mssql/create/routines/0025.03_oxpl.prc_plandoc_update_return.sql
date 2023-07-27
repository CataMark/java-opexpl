create or alter procedure oxpl.prc_plandoc_update_return
    @json nvarchar(max),
    @kid varchar(20)
as
    begin
        set nocount on;
        exec sys.sp_set_session_context @key = N'oxpl_user_id', @value = @kid, @readonly = 0;
        
        begin try
            begin transaction
                declare @id table(head_id uniqueidentifier not null, data_set int not null);

                /* actualizare document */
                update a
                set a.descr = b.descr, a.cheie = b.cheie, a.opex_categ = b.opex_categ, a.ic_part = b.ic_part, a.mod_de = @kid, a.mod_timp = current_timestamp
                output inserted.id, inserted.data_set into @id (head_id, data_set)
                from oxpl.tbl_int_recs_plan_head as a
                inner join openjson(@json) with (
                        id uniqueidentifier '$.id',
                        descr nvarchar(2000) '$.descr',
                        cheie int '$.cheie',
                        opex_categ int '$.opex_categ',
                        ic_part varchar(5) '$.ic_part'
                    ) as b
                on a.id = b.id;

                /* actualizare valori planificare */
                declare @head_id uniqueidentifier;
                declare @data_set int;
                select top 1 @head_id = a.head_id, @data_set = a.data_set from @id as a;

                merge into oxpl.tbl_int_recs_plan_vals as t
                using (select a.*, b.*
                        from openjson(json_query(@json, '$.valori')) with(
                            id uniqueidentifier '$.id',
                            an smallint '$.an',
                            per char(2) '$.per',
                            valoare float '$.valoare'
                        ) as a
                        cross apply @id as b
                        inner join oxpl.tbl_int_data_set_per as c
                        on b.data_set = c.data_set and a.an = c.an and a.per = c.per
                        where c.actual = 0 and a.valoare !=0) as s
                on (t.id = s.id)
                when matched then
                    update set
                        t.an = s.an,
                        t.per = s.per,
                        t.valoare = s.valoare,
                        t.mod_de = @kid,
                        t.mod_timp = current_timestamp
                when not matched by target then
                    insert(head_id, cont, data_set, an, per, valoare, mod_de, mod_timp)
                    values (s.head_id, null, s.data_set, s.an, s.per, s.valoare, @kid, current_timestamp)
                when not matched by source and t.data_set = @data_set and t.head_id = @head_id and
                    exists (select * from oxpl.tbl_int_data_set_per as a where a.data_set = @data_set and a.an = t.an and a.per = t.per and a.actual = 0) then
                    delete;

                /* obtine raspuns */
                select b.*
                from @id as a
                cross apply (select * from oxpl.fnc_plandoc_get_by_id(a.head_id)) as b;

            commit transaction
        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            throw;
        end catch
    end;