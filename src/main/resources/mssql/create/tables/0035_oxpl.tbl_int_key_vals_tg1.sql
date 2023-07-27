create or alter trigger oxpl.tbl_int_key_vals_tg1
on oxpl.tbl_int_key_vals
after insert, update, delete
as
    begin
        set nocount on;
        begin try
            if exists (select * from inserted as i
                        inner join oxpl.tbl_int_key_head as a
                        on i.cheie = a.id
                        inner join oxpl.tbl_int_key_type as b
                        on a.ktype = b.cod
                        inner join oxpl.tbl_int_coarea as c
                        on i.coarea = c.cod
                        where i.coarea != a.coarea or
                            1 = (case
                                    when b.general = 1 and a.ktype = 'G01' then
                                        iif(i.hier != c.cc_hier or i.data_set != i.gen_data_set, 1, 0)
                                    when b.general = 1 and a.ktype != 'G01' then
                                        iif(i.cost_centre is not null, 1, 0)
                                    when b.general = 0 then
                                        iif(i.hier != a.hier or i.data_set != a.data_set or i.cost_centre != a.cost_centre or i.data_set != i.gen_data_set, 1, 0)
                                else 0 end))
                raiserror('Poziţiile introduse nu respectă aria de controlling sau centrul de cost ale cheii!', 16, 1);

            if exists (select * from inserted as i
                        inner join oxpl.tbl_int_ccntr as a
                        on i.hier = a.hier and i.data_set = a.data_set and i.cost_centre = a.cod
                        where a.blocat = 1)
                raiserror ('Nu se pot introduce valori pe centre de cost blocate!', 16, 1);

            if exists (select * from inserted as i
                        left join (select distinct m.data_set, m.an
                                    from oxpl.tbl_int_data_set_per as m) as a
                        on i.gen_data_set = a.data_set and i.an = a.an
                        where a.an is null)
                raiserror ('Anul introdus nu se regăseşte in perioadele setului de date!', 16, 1);

            --backup
            insert into oxpl.tbl_int_backup(tabela, data_set, coarea, hier, row_id, json_record, mod_de, mod_timp)
            select 'tbl_int_key_vals' as tabela,
                d.gen_data_set,
                d.coarea,
                d.hier,
                d.id as row_id,
                (select * from deleted as a where a.id = d.id for json auto, without_array_wrapper) as json_record,
                cast(session_context(N'oxpl_user_id') as varchar(20)) as mod_de,
                current_timestamp as mod_timp
            from deleted as d;

        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            throw;
        end catch
    end;