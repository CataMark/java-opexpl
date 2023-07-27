create or alter procedure oxpl.prc_cheie_c01_val_delete_by_key
    @cheie int,
    @set int,
    @kid varchar(20)
as
    begin
        set nocount on;
        exec sys.sp_set_session_context @key = N'oxpl_user_id', @value = @kid, @readonly = 0;
        delete from oxpl.tbl_int_key_vals where cheie = @cheie and gen_data_set = @set;

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
        where a.id = @cheie
        order by a.id asc;
    end;