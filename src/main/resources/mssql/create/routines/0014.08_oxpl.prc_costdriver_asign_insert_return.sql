create or alter procedure oxpl.prc_costdriver_asign_insert_return
    @coarea char(4),
    @cost_driver char(5),
    @blocat bit,
    @kid varchar(20)
as
    begin
        set nocount on;
        declare @id table (id uniqueidentifier);

        insert into oxpl.tbl_int_cost_driver_assign (coarea, cost_driver, blocat, mod_de, mod_timp)
        output inserted.id into @id
        values (@coarea, @cost_driver, @blocat, @kid, current_timestamp);

        select b.*
        from @id as a
        cross apply (select * from oxpl.fnc_costdriver_asign_get_by_id(a.id)) as b;
    end;