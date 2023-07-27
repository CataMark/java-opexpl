create or alter procedure oxpl.prc_costcentergroup_insert_return
    @hier char(5),
    @set int,
    @cod varchar(10),
    @nume nvarchar(50),
    @superior uniqueidentifier,
    @kid varchar(20)
as
    begin
        set nocount on;
        declare @id table(id uniqueidentifier);

        insert into oxpl.tbl_int_ccntr_grup (hier, data_set, cod, nume, superior, mod_de, mod_timp)
        output inserted.id into @id
        values (@hier, @set, @cod, @nume, @superior, @kid, current_timestamp);

        select b.*
        from @id as a
        cross apply (select * from oxpl.fnc_costcentergroup_get_by_id(a.id)) as b;
    end;