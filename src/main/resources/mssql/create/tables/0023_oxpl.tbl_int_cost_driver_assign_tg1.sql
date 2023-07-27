create or alter trigger oxpl.tbl_int_cost_driver_assign_tg1
on oxpl.tbl_int_cost_driver_assign
after update, delete
as
    begin
        set nocount on;
        begin try

            if exists (select * from inserted)
                update a
                    set a.blocat = i.blocat, a.mod_de = i.mod_de, a.mod_timp = current_timestamp
                from oxpl.tbl_int_opex_categ_assign as a
                inner join oxpl.tbl_int_opex_categ as b
                on a.opex_categ = b.cod
                inner join inserted as i
                on b.cost_driver = i.cost_driver
                where a.coarea = i.coarea;
            else if exists (select * from deleted as d
                            inner join oxpl.tbl_int_opex_categ as a
                            on d.cost_driver = a.cost_driver
                            inner join oxpl.tbl_int_opex_categ_assign as b
                            on a.cod = b.opex_categ
                            where d.coarea = b.coarea)
                raiserror('Există categorii de cheltuieli aferente cost driver-ului asignate pe aria de controlling în scop!', 16, 1);

        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            throw;
        end catch
    end;