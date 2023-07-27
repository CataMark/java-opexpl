create or alter procedure oxpl.prc_opexcateg_insert_return
    @nume nvarchar(50),
    @cost_driver char(5),
    @cont_ccoa char(10),
    @kid varchar(20)
as
    begin
    set nocount on;
        declare @id table (cod int);

        insert into oxpl.tbl_int_opex_categ (nume, cost_driver, cont_ccoa, mod_de, mod_timp)
        output inserted.cod into @id
        values (@nume, @cost_driver, @cont_ccoa, @kid, current_timestamp);

        select b.*
        from @id as a
        cross apply (select * from oxpl.fnc_opexcateg_get_by_cod(a.cod)) as b;
    end;