create or alter function oxpl.fnc_cheie_g02_get_by_id_aggr(
    @id int,
    @set int
)
returns table
as
return
    select top 1
        a.*,
        cast((select distinct
            m.an,
            sum(m.valoare) over (partition by m.an) as valoare,
            first_value(m.mod_de) over (partition by m.cheie order by m.mod_timp desc) as mod_de,
            max(m.mod_timp) over (partition by m.cheie) as mod_timp
        from oxpl.tbl_int_key_vals as m
        where m.cheie = a.id and m.gen_data_set = @set
        order by m.an asc
        for json path) as nvarchar(max)) as valori
    from oxpl.tbl_int_key_head as a
    where a.id = @id;