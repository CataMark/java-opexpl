create or alter trigger oxpl.tbl_int_ccntr_tg1
on oxpl.tbl_int_ccntr
after insert, update, delete
as
    begin
        set nocount on;
        begin try
            --verificare ca nu exista un grup cu acelasi cod
            if exists (select * from inserted as i
                        inner join oxpl.tbl_int_ccntr_grup as a
                        on i.hier = a.hier and i.data_set = a.data_set and i.cod = a.cod)
                raiserror('Există deja un grup cu acelaşi cod!', 16, 1);

            --backup
            declare @not_perform_backup bit = cast(session_context(N'oxpl_not_backup') as bit);
            if @not_perform_backup is null or @not_perform_backup != 1
                insert into oxpl.tbl_int_backup(tabela, data_set, hier, row_id, json_record, mod_de, mod_timp)
                select 'tbl_int_ccntr' as tabela,
                    d.data_set,
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