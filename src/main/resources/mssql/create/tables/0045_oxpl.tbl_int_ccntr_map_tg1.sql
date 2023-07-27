create or alter trigger oxpl.tbl_int_ccntr_map_tg1
on oxpl.tbl_int_ccntr_map
after insert, update, delete
as
    begin
        begin try
            if exists (select * from inserted as i
                        inner join oxpl.tbl_int_data_set as a
                        on i.data_set = a.id
                        inner join oxpl.tbl_int_plan_vers as b
                        on a.vers = b.cod
                        where b.actual = 1)
                raiserror('Nu este necesară maparea pentru seturi de date de actual!', 16, 1);

            if exists (select * from inserted as i
                        inner join oxpl.tbl_int_data_set as a
                        on i.data_set = a.id
                        where a.impl_compare is null and a.actual_set is null)
                raiserror('Setul de date nu are referinţe la alte seturi! Nu este necesară maparea!', 16, 1);

            if exists (select * from
                            (select distinct hier, data_set from inserted
                            union
                            select distinct hier, data_set from deleted) as i
                        inner join oxpl.tbl_int_recs_plan_head as a
                        on i.hier = a.hier and i.data_set = a.data_set
                        inner join oxpl.tbl_int_recs_plan_vals as b
                        on a.id = b.head_id
                        inner join oxpl.tbl_int_data_set_per as c
                        on b.data_set = c.data_set and b.an = c.an and b.per = c.per
                        where c.actual = 1)
                raiserror('Există deja valori pe perioadele de actual pentru setul de date şi aria de controlling! Maparea nu mai poate fi modificată!', 16, 1);

        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            throw;
        end catch
    end;