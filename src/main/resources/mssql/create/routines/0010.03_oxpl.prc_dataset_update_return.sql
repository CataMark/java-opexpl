create or alter procedure oxpl.prc_dataset_update_return
    @id int,
    @nume nvarchar(30),
    @vers char(3),
    @blocat bit,
    @incheiat bit,
    @raportare bit,
    @compar int,
    @actset int,
    @kid varchar(20)
as
    begin
        set nocount on;

        update oxpl.tbl_int_data_set
        set nume = @nume, vers = @vers, blocat = @blocat, incheiat = @incheiat, raportare = @raportare, impl_compare = @compar, actual_set = @actset, mod_de = @kid, mod_timp = current_timestamp
        where id = @id;

        select * from oxpl.fnc_dataset_get_by_id(@id);
    end;