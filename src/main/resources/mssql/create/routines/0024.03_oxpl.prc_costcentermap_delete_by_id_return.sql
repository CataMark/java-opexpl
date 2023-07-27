create or alter procedure oxpl.prc_costcentermap_delete_by_id_return
    @id uniqueidentifier
as
    begin
        set nocount on;
        delete from oxpl.tbl_int_ccntr_map where id = @id;

        /* testare operatiune reusita */
        if exists (select * from oxpl.tbl_int_ccntr_map where id = @id)
            select cast(0 as bit) as rezultat;
        else
            select cast(1 as bit) as rezultat;
    end;