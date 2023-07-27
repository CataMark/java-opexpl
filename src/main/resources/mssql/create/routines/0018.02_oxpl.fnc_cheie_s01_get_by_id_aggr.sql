create or alter function oxpl.fnc_cheie_s01_get_by_id_aggr(
    @id int
)
returns table
as
return
    select top 1
        a.*,
        b.nume as cost_centre_nume,
        cast((select
            m.an,
            sum(m.valoare) as valoare
        from oxpl.tbl_int_key_vals as m
        where m.cheie = a.id
        group by m.an
        order by m.an asc
        for json path) as nvarchar(max)) as valori
    from oxpl.tbl_int_key_head as a

    inner join oxpl.tbl_int_ccntr as b
    on a.hier = b.hier and a.data_set = b.data_set and a.cost_centre = b.cod
    
    where a.id = @id;