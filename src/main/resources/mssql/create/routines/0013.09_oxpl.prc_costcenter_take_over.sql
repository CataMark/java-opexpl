create or alter procedure oxpl.prc_costcenter_take_over
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

            if exists (select * from oxpl.tbl_int_ccntr where hier = @hier and data_set = @dest_set)
                raiserror('Există deja centre de cost în setul de date destinaţie!', 16, 1);

            if exists (select *
                        from (select * from oxpl.tbl_int_ccntr where hier = @hier and data_set = @from_set) as a
                        left join (select * from oxpl.tbl_int_ccntr_grup where hier = @hier and data_set = @dest_set) as b
                        on a.grup = b.cod
                        where b.cod is null)
                raiserror('În setul de date destinaţie nu există toate grupurile necesare centrelor de cost din setul de date sursă!', 16, 1);
            
            begin transaction
                insert into oxpl.tbl_int_ccntr(hier, data_set, cod, nume, grup, blocat, mod_de, mod_timp)
                select
                    a.hier,
                    @dest_set,
                    a.cod,
                    a.nume,
                    a.grup,
                    a.blocat,
                    @kid,
                    current_timestamp
                from oxpl.tbl_int_ccntr as a
                where a.hier = @hier and a.data_set = @from_set;
            commit transaction
        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            throw;
        end catch
    end;