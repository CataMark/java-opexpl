create or alter procedure oxpl.prc_dataset_has_values
    @id int
as
    begin
        if exists (select * from oxpl.tbl_int_recs_plan_vals where data_set = @id)
            select cast(1 as bit) as rezultat;
        else
            select cast(0 as bit) as rezultat;
    end;