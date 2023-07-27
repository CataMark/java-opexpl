create or alter trigger oxpl.tbl_int_recs_plan_head_tg1
on oxpl.tbl_int_recs_plan_head
after insert, update, delete
as
    begin
        set nocount on;
        begin try
            if exists (select * from inserted as i
                        inner join oxpl.tbl_int_coarea as a
                        on i.coarea = a.cod
                        where i.hier != a.cc_hier)
                raiserror('Ierarhia de centre de cost nu este conformă cu aria de controlling!', 16, 1);

            if exists (select * from inserted as i
                        inner join oxpl.tbl_int_key_head as a
                        on i.cheie = a.id
                        inner join oxpl.tbl_int_key_type as b
                        on a.ktype = b.cod
                        where i.coarea != a.coarea or
                        1 = (case when b.general = 1 then 0
                            else iif(i.data_set = a.data_set and i.hier = a.hier and i.cost_centre = a.cost_centre, 0, 1) end))
                raiserror('Aria de controlling sau setul de date nu sunt conforme cu cheia de alocare!', 16, 1);

            if exists(select * from inserted as i
                        inner join oxpl.tbl_int_ccntr as a
                        on i.hier = a.hier and i.data_set = a.data_set and i.cost_centre = a.cod
                        where a.blocat = 1)
                raiserror('Nu pot fi introduse înregistrări pe centre de cost blocate!', 16, 1);

            if exists (select * from inserted as i
                        inner join oxpl.tbl_int_opex_categ_assign as a
                        on i.coarea = a.coarea and i.opex_categ = a.opex_categ
                        where a.blocat = 1)
                raiserror('Nu pot fi introduse înregistrări pe categorii de cheltuieli blocate!', 16, 1);

            --backup
            declare @not_perform_backup bit = cast(session_context(N'oxpl_not_backup') as bit);
            if @not_perform_backup is null or @not_perform_backup != 1
                insert into oxpl.tbl_int_backup(tabela, data_set, coarea, hier, row_id, json_record, mod_de, mod_timp)
                select 'tbl_int_recs_plan_head' as tabela,
                    d.data_set,
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