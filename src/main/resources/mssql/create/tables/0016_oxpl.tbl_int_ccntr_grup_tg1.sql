create or alter trigger oxpl.tbl_int_ccntr_grup_tg1
on oxpl.tbl_int_ccntr_grup
after insert, update, delete
as
    begin
        set nocount on;
        begin try
            --constraingeri
            if exists (select * from inserted where superior is null)
                and exists (select * from oxpl.tbl_int_ccntr_grup as a
                        inner join inserted as i
                        on a.data_set = i.data_set and a.hier = i.hier and a.id != i.id
                        where a.superior is null)
                raiserror('Ierarhia poate avea doar o singură rădăcină!', 16, 1);

            if exists (select * from inserted where superior is null and cod != hier)
                raiserror('Grupul rădăcină al ierarhiei treuie să aibă acelaşi cod ca şi ierarhia!', 16, 1);

            if exists (select * from inserted as i
                        inner join oxpl.tbl_int_ccntr_grup as a
                        on i.superior = a.id
                        where i.hier != a.hier or i.data_set != a.data_set)
                raiserror('Superiorul nu există în ierarhia şi setul de date ale noului grup!', 16, 1);

            --verificare ca nu exista un centru de cost cu acelasi cod
            if exists (select * from inserted as i
                        inner join oxpl.tbl_int_ccntr as a
                        on i.hier = a.hier and i.data_set = a.data_set and i.cod = a.cod)
                raiserror('Există deja un centru de cost cu acest cod!', 16, 1);

            --backup
            declare @not_perform_backup bit = cast(session_context(N'oxpl_not_backup') as bit);
            if @not_perform_backup is null or @not_perform_backup != 1
                insert into oxpl.tbl_int_backup(tabela, data_set, hier, row_id, json_record, mod_de, mod_timp)
                select 'tbl_int_ccntr_grup' as tabela,
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