create or alter trigger oxpl.tbl_int_key_rule_tg1
on oxpl.tbl_int_key_rule
after insert, update, delete
as
    begin
        begin try
            if exists (select * from inserted as i
                        inner join oxpl.tbl_int_key_head as a
                        on i.cheie = a.id
                        inner join oxpl.tbl_int_key_type as b
                        on a.ktype = b.cod
                        where b.calculat = 0)
                raiserror('Cheia pentru care se doreşte adăugarea regulii nu are funcţionalităţi de calcul!', 16, 1);

            if exists (select * from inserted)
                begin
                    declare @check table (
                        medie_pond bit not null,
                        chei_poz int not null,
                        centre_poz int not null,
                        categ_poz int not null,
                        part_poz int not null
                    );

                    insert into @check (medie_pond, chei_poz, centre_poz, categ_poz, part_poz)
                    select
                        i.medie_pond, a.pozitii, b.pozitii, c.pozitii, d.pozitii
                    from inserted as i
                    cross apply (select count(*) as pozitii from openjson(i.chei_json)) as a
                    cross apply (select count(*) as pozitii from openjson(i.cost_centre_json)) as b
                    cross apply (select count(*) as pozitii from openjson(i.opex_categ_json)) as c
                    cross apply (select count(*) as pozitii from openjson(i.ic_part_json)) as d;

                    if exists (select * from @check as a
                                where a.chei_poz = 0)
                        raiserror('Regula de calcul trebuie să conţină cel puţin o cheie de alocare sursă!', 16, 1);

                    if exists (select * from @check as a
                                where (a.chei_poz > 1 or a.categ_poz != 0 or a.part_poz != 0)
                                    and a.medie_pond = 0)
                        raiserror('Media aritmetică poate fi calculată doar atunci când regula conţine doar o singură cheie de alocare cu sau fară centre de cost!', 16, 1);
                end;               

        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            throw;
        end catch
    end;