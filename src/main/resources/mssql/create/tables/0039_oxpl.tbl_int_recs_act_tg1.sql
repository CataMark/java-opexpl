create or alter trigger oxpl.tbl_int_recs_act_tg1
on oxpl.tbl_int_recs_act
after insert, update, delete
as
    begin
        set nocount on;
        begin try
            if exists (select * from inserted as i
                        inner join oxpl.tbl_int_opex_categ_assign as a
                        on i.coarea = a.coarea and i.opex_categ = a.opex_categ
                        where a.blocat = 1)
                raiserror('Nu se pot introduce înregistrări pe categorii de cheltuieli blocate!', 16, 1);

            if exists (select * from inserted as i
                        inner join oxpl.tbl_int_data_set as a
                        on i.data_set = a.id
                        inner join oxpl.tbl_int_plan_vers as b
                        on a.vers = b.cod
                        where b.actual = 0)
                raiserror('Nu se pot introduce valori realizate pe seturi de date de planificare!', 16, 1);
        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            throw;
        end catch
    end;