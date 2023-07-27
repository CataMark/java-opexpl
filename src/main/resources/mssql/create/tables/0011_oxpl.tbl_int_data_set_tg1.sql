create or alter trigger oxpl.tbl_int_data_set_tg1
on oxpl.tbl_int_data_set
after insert, update
as
    begin
        set nocount on;

        begin try
            if exists (select * from inserted as i
                        inner join oxpl.tbl_int_data_set as a
                        on i.actual_set = a.id
                        inner join oxpl.tbl_int_plan_vers as b
                        on a.vers = b.cod
                        where b.actual = 0)
                raiserror('Nu se pot adăuga seturi de date pentru planificare ca şi set de date din care se vor prelua valorile realizate!', 16, 1);

            if exists (select * from inserted as i
                        inner join oxpl.tbl_int_data_set_per as a
                        on i.id = a.data_set
                        left join oxpl.tbl_int_data_set_per as c
                        on i.actual_set = c.data_set and a.an = c.an and a.per = c.per
                        where i.actual_set is not null and a.actual = 1 and c.per is null)
                raiserror('Există perioade asignate setului de date care nu se regăsesc în setul de date pentru preluarea valorilor realizate!', 16, 1);

            if exists (select *
                        from (select * from inserted where raportare = 0) as i

                        inner join (select * from deleted where raportare = 1) as d
                        on i.id = d.id

                        inner join (select distinct impl_compare from oxpl.tbl_int_data_set where impl_compare is not null) as a
                        on d.id = a.impl_compare)
                raiserror('Setul de date nu poate fi scos din raportare deoarece este folosit ca și comparatie implicită!', 16, 1);

            if exists (select * from inserted as i
                        inner join oxpl.tbl_int_data_set as a
                        on i.impl_compare = a.id
                        where i.impl_compare is not null and a.raportare = 0)
                raiserror('Setul de date nu poate fi adăugat ca și comparație implicită deoarece nu este marcat pentru raportare!', 16, 1);
            
            --blocare seturi de date de actual
            update a
            set a.blocat = 1, a.impl_compare = null, a.actual_set = null, a.raportare = 0
            from oxpl.tbl_int_data_set as a
            inner join inserted as i
            on a.id = i.id
            inner join oxpl.tbl_int_plan_vers as b
            on a.vers = b.cod
            where b.actual = 1;

            --setare finalizat
            update a
            set a.incheiat = 0
            from oxpl.tbl_int_data_set as a
            inner join inserted as i
            on a.id = i.id
            where i.blocat = 0;

        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            throw;
        end catch
    end;