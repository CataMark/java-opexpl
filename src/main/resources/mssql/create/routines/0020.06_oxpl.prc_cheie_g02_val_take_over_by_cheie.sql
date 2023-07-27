create or alter procedure oxpl.prc_cheie_g02_val_take_over_by_cheie
    @cheie int,
    @from_set int,
    @dest_set int,
    @kid varchar(20)
as
    begin
        set nocount on;
        begin try
            if exists (select * from oxpl.tbl_int_key_vals as a
                        where a.cheie = @cheie and a.gen_data_set = @dest_set)
                raiserror('Există deja valori pe cheie!', 16, 1);
            
            /* stabilire ani calcul */
            declare @ani_calcul table (an smallint not null);
            insert into @ani_calcul(an)
            select
                a.an
            from (select distinct an from oxpl.tbl_int_data_set_per where data_set = @dest_set) as a
            inner join (select distinct an from oxpl.tbl_int_data_set_per where data_set = @from_set) as b
            on a.an = b.an
            order by a.an asc;

            if not exists (select * from @ani_calcul)
                raiserror('Nu există ani comuni în perioadele celor 2 seturi de date: sursă şi destinatar!',16, 1);

            if not exists (select * from oxpl.tbl_int_key_vals as a
                            inner join @ani_calcul as b
                            on a.an = b.an
                            where a.cheie = @cheie and a.gen_data_set = @from_set)
                raiserror('Nu există valori pe cheie în setul de date sursă pe anii comuni cu cei ai setului de date destinatar!', 16, 1);

            begin transaction
                insert into oxpl.tbl_int_key_vals (cheie, coarea, buss_line, gen_data_set, an, valoare, mod_de, mod_timp)
                select
                    a.cheie,
                    a.coarea,
                    a.buss_line,
                    @dest_set as gen_data_set,
                    a.an,
                    a.valoare,
                    @kid as mod_de,
                    current_timestamp as mod_timp
                from oxpl.tbl_int_key_vals as a
                inner join @ani_calcul as b
                on a.an = b.an
                where a.cheie = @cheie and a.gen_data_set = @from_set;

                /* obtine raspuns */
                select * from oxpl.fnc_cheie_g02_get_by_id_aggr(@cheie, @dest_set);
            commit transaction
        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            throw;
        end catch
    end;