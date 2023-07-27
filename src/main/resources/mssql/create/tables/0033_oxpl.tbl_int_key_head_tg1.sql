create or alter trigger oxpl.tbl_int_key_head_tg1
on oxpl.tbl_int_key_head
after insert, update
as
    begin
        begin try
            if exists (select * from inserted as i
                        inner join oxpl.tbl_int_key_type as a
                        on i.ktype = a.cod
                        where a.general = 1 and
                        (i.data_set is not null or i.hier is not null or i.cost_centre is not null))
                raiserror('Pentru cheile generale nu trebuie completate setul de date, ierarhia şi centrul de cost!', 16, 1);

            if exists (select * from inserted as i
                        inner join oxpl.tbl_int_key_type as a
                        on i.ktype = a.cod
                        inner join oxpl.tbl_int_coarea as b
                        on i.coarea = b.cod
                        where a.general = 0 and
                        (i.data_set is null or i.hier is null or i.cost_centre is null or i.hier != b.cc_hier))
                raiserror('Pentru cheile specifice trebuie completate setul de date, ierarhia conform ariei de controlling şi centrul de cost!', 16, 1);

        end try
        begin catch
            rollback transaction;
            throw;
        end catch
    end;