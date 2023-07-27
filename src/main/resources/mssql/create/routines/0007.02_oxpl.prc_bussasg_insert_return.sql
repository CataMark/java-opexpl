create or alter procedure oxpl.prc_bussasg_insert_return
    @coarea char(4),
    @bussline char(4),
    @kid varchar(20)
as
    begin
        set nocount on;
        declare @id table (id uniqueidentifier);

        insert into oxpl.tbl_int_buss_line_asg (coarea, buss_line, mod_de, mod_timp)
        output inserted.id into @id
        values (@coarea, @bussline, @kid, current_timestamp);

        select b.*
        from @id as a
        cross apply (select * from oxpl.fnc_bussasg_get_by_id(a.id)) as b;
    end;