create or alter trigger oxpl.tbl_int_plan_vers_tg1
on oxpl.tbl_int_plan_vers
after insert, update, delete
as
    BEGIN
        set nocount on;
        begin try
            if exists (select * from inserted where actual = 1)
                raiserror('Versiunea de valori actuale este predefinită şi nu poate fi modificată!', 16, 1);

            if exists (select * from deleted where actual = 1)
                raiserror('Versiunea de valori actuale este predefinită şi nu poate fi ştearsă!', 16, 1);
        end try
        begin catch
            rollback transaction;
            throw;
        end catch
    end;