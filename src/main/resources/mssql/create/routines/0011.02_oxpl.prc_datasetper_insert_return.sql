create or alter procedure oxpl.prc_datasetper_insert_return
    @set int,
    @an smallint,
    @per char(2),
    @actual bit,
    @kid varchar(20)
as
    begin
        set nocount on;
        declare @id table (id uniqueidentifier);

        insert into oxpl.tbl_int_data_set_per (data_set, an, per, actual, mod_de, mod_timp)
        output inserted.id into @id
        values (@set, @an, @per, @actual, @kid, current_timestamp);

        select b.*
        from @id as a
        cross apply (select * from oxpl.fnc_datasetper_get_by_id(a.id)) as b;
    end;