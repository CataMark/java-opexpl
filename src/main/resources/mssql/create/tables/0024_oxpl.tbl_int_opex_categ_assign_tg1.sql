create or alter trigger oxpl.tbl_int_opex_categ_assign_tg1
on oxpl.tbl_int_opex_categ_assign
after insert, update
as
    begin
        set nocount on;
        begin try
            if exists (select * from inserted as i
                        inner join oxpl.tbl_int_opex_categ as a
                        on i.opex_categ = a.cod
                        outer apply (select b.cost_driver
                                    from oxpl.tbl_int_cost_driver_assign as b
                                    where b.cost_driver = a.cost_driver and b.coarea = i.coarea) as c
                        where c.cost_driver is null)
                raiserror('Cost driver-ul aferente categoriei nu este asignat pe aria de controlling în scop!', 16, 1);

            if exists (select * from inserted as i
                        inner join oxpl.tbl_int_opex_categ as a
                        on i.opex_categ = a.cod
                        inner join oxpl.tbl_int_cost_driver_assign as b
                        on a.cost_driver = b.cost_driver
                        where i.coarea = b.coarea and b.blocat = 1 and i.blocat = 0)
                raiserror('Cost driver-ul este blocat la înregistrare pentru aria de controlling în scop! Categoria opex nu poate fi deblocată!', 16, 1);

        end try
        begin catch
            rollback transaction;
            throw;
        end catch
    end;