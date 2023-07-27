create or alter trigger oxpl.tbl_int_users_cost_drivers_tg1
on oxpl.tbl_int_users_cost_drivers
after insert, update
as
    begin
        set nocount on;
        begin try
            if exists (select * from inserted as i
                    left join (select a.uname from portal.tbl_int_ugroups as a
                                inner join oxpl.tbl_int_ugrup as b
                                on a.ugroup = b.cod
                                where b.cost_driver_bound = 1) as x
                    on i.uname = x.uname
                    where x.uname is null)
                raiserror('Se pot adăuga doar utilizatori aparţinând grupurilor ce sunt limitate în funcţie de cost driver!', 16, 1);

        end try
        begin catch
            rollback transaction;
            throw;
        end catch
    end;