create or alter procedure oxpl.prc_cheie_g01_def_insert_return
    @nume nvarchar(50),
    @descr nvarchar(4000),
    @coarea char(4),
    @blocat bit,
    @kid varchar(20)
as
    begin
        set nocount on;
        declare @id table (id int);

        insert into oxpl.tbl_int_key_head(nume, descr, coarea, ktype, blocat, mod_de, mod_timp)
        output inserted.id into @id
        values (@nume, @descr, @coarea, 'G01', @blocat, @kid, current_timestamp);

        select top 1
            a.*
        from oxpl.tbl_int_key_head as a
        inner join @id as b
        on a.id = b.id;
    end;