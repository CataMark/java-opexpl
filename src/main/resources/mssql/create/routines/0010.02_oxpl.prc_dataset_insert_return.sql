create or alter procedure oxpl.prc_dataset_insert_return
    @nume nvarchar(30),
    @an smallint,
    @vers char(3),
    @compar int,
    @actset int,
    @kid varchar(20)
as
    begin
        set nocount on;
        declare @id table (id int);

        insert into oxpl.tbl_int_data_set (nume, an, vers, blocat, incheiat, raportare, impl_compare, actual_set, mod_de, mod_timp)
        output inserted.id into @id
        values (@nume, @an, @vers, 1, 1, 0, @compar, @actset, @kid, current_timestamp);

        select b.*
        from @id as a
        cross apply (select * from oxpl.fnc_dataset_get_by_id(a.id)) as b;
    end;