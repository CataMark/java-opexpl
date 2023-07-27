create or alter procedure oxpl.prc_costcentermap_delete_all_return
    @hier char(5),
    @set int
as
    begin
        set nocount on;
        delete from oxpl.tbl_int_ccntr_map where hier = @hier and data_set = @set;

        /* testare operatiune reusita */
        if exists (select *  from oxpl.tbl_int_ccntr_map where hier = @hier and data_set = @set)
            select cast(0 as bit) as rezultat;
        else
            select cast(1 as bit) as rezultat;
    end;