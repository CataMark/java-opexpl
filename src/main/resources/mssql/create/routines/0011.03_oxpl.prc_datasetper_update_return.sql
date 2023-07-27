create or alter procedure oxpl.prc_datasetper_update_return
    @id uniqueidentifier,
    @actual bit,
    @kid varchar(20)
as
    begin
        set nocount on;

        update oxpl.tbl_int_data_set_per
        set actual = @actual, mod_de = @kid, mod_timp = current_timestamp
        where id = @id;

        select * from oxpl.fnc_datasetper_get_by_id(@id);
    end;