create or alter procedure oxpl.prc_cheie_g01_def_update_return
    @set int,
    @id int,
    @nume nvarchar(50),
    @descr nvarchar(4000),
    @blocat bit,
    @kid varchar(20)
as
    begin
        set nocount on;

        update oxpl.tbl_int_key_head
        set nume = @nume, descr = @descr, blocat = @blocat, mod_de = @kid, mod_timp = current_timestamp
        where id = @id;

        select
            a.*,
            cast((select distinct
                n.an,
                sum(n.valoare) over (partition by n.an) as valoare,
                first_value(n.mod_de) over (partition by n.cheie order by n.mod_timp desc) as mod_de,
                max(n.mod_timp) over (partition by n.cheie) as mod_timp
            from oxpl.tbl_int_key_vals as n
            where n.cheie = a.id and n.gen_data_set = @set
            order by n.an asc
            for json path) as nvarchar(max)) as valori
        from oxpl.tbl_int_key_head as a
        where a.id = @id;
    end;