create or alter procedure oxpl.prc_costcenter_insert_return
    @hier char(5),
    @set int,
    @cod varchar(10),
    @nume nvarchar(100),
    @grup varchar(10),
    @blocat bit,
    @kid varchar(10)
as
    begin
        set nocount on;
        declare @id table (id uniqueidentifier);

        insert into oxpl.tbl_int_ccntr (hier, data_set, cod, nume, grup, blocat, mod_de, mod_timp)
        output inserted.id into @id
        values (@hier, @set, @cod, @nume, @grup, @blocat, @kid, current_timestamp);

        select b.*
        from @id as a
        cross apply (select * from oxpl.fnc_costcenter_get_by_id(a.id)) as b;
    end;