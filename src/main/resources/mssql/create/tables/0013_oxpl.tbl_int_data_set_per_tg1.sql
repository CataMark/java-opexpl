create or alter trigger oxpl.tbl_int_data_set_per_tg1
on oxpl.tbl_int_data_set_per
after insert, update, delete
as
    begin
        begin try            
            if exists (select * from inserted as i
                        inner join oxpl.tbl_int_data_set as a
                        on i.data_set = a.id
                        where a.incheiat = 1)
                or exists (select * from deleted as d
                        inner join oxpl.tbl_int_data_set as a
                        on d.data_set = a.id
                        where a.incheiat = 1)
                raiserror('Seturile de date încheiate nu pot fi modificate!', 16, 1);

            if exists (select * from inserted as i
                        inner join oxpl.tbl_int_recs_plan_vals as a
                        on i.data_set = a.data_set and i.an = a.an and i.per = a.per)
                raiserror('Nu pot fi modificate perioadele pentru care există deja valori aferente setului de date în scop!', 16, 1);

            if exists (select * from inserted as i
                        inner join oxpl.tbl_int_data_set as a
                        on i.data_set = a.id
                        inner join oxpl.tbl_int_plan_vers as b
                        on a.vers = b.cod
                        where b.actual = 1 and i.an != a.an)
                raiserror('Seturile de date cu versiune de actual trebuie să conţină perioade doar pentru anul introdus în definiţia acestuia!', 16, 1);

            if exists (select * from inserted as i
                        inner join oxpl.tbl_int_data_set as a
                        on i.data_set = a.id
                        left join oxpl.tbl_int_data_set_per as b
                        on a.actual_set = b.data_set and i.an = b.an and i.per = b.per
                        where i.actual = 1 and b.per is null)
                raiserror('Perioadele de actual trebuie să fie dintre cele aferente setului de date setat pentru a prelua valorile realizate!', 16, 1);
        end try
        begin catch
            rollback transaction;
            throw;
        end catch
    end;