create or alter procedure oxpl.prc_opexcateg_asign_insert_return
    @coarea char(4),
    @opex_categ int,
    @blocat bit,
    @kid varchar(20)
as
    begin
        set nocount on;
        declare @id table(id uniqueidentifier);

        insert into oxpl.tbl_int_opex_categ_assign (coarea, opex_categ, blocat, mod_de, mod_timp)
        output inserted.id into @id
        values (@coarea, @opex_categ, @blocat, @kid, current_timestamp);

        select b.*
        from @id as a
        cross apply (select * from oxpl.fnc_opexcateg_asign_get_by_id(a.id)) as b;
    end;