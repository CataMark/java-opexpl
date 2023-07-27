create or alter procedure oxpl.prc_costcentergroup_take_over
    @hier char(5),
    @from_set int,
    @dest_set int,
    @kid varchar(20)
as
    begin
        set nocount on;
        begin try
            if @from_set = @dest_set
                raiserror('Setul de date sursă nu poate fi acelaşi cu cel destinatie!', 16, 1);

            if exists (select * from oxpl.tbl_int_ccntr_grup where hier = @hier and data_set = @dest_set)
                raiserror('Există deja grupuri de centre de cost în setul de date destinaţie!', 16, 1);
            
            begin transaction
                declare @id_match table(
                    nid uniqueidentifier,
                    vid uniqueidentifier,
                    contor int
                );
                declare @contor int = 0;

                while (@contor = 0 or exists (select * from @id_match))
                    begin
                        if @contor = 0
                            merge into oxpl.tbl_int_ccntr_grup as t
                            using (select *
                                    from oxpl.tbl_int_ccntr_grup as a
                                    where a.hier = @hier and a.data_set = @from_set and a.superior is null) as s
                            on 1 = 0
                            when not matched by target then
                                insert (hier, data_set, cod, nume, superior, mod_de, mod_timp)
                                values (s.hier, @dest_set, s.cod, s.nume, s.superior, @kid, current_timestamp)
                                output inserted.id, s.id, @contor into @id_match;

                        else
                            merge into oxpl.tbl_int_ccntr_grup as t
                            using (select a.*, b.nid
                                    from oxpl.tbl_int_ccntr_grup as a
                                    inner join @id_match as b
                                    on a.superior = b.vid
                                    where a.hier = @hier and a.data_set = @from_set) as s
                            on 1 = 0
                            when not matched by target then
                                insert (hier, data_set, cod, nume, superior, mod_de, mod_timp)
                                values (s.hier, @dest_set, s.cod, s.nume, s.nid, @kid, current_timestamp)
                                output inserted.id, s.id, @contor into @id_match;

                        delete a from @id_match as a where a.contor != @contor;
                        set @contor = @contor + 1;
                    end;
            commit transaction
        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            throw;
        end catch
    end;