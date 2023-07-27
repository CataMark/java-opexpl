create or alter procedure oxpl.prc_cheie_get_list_asign_no_val
    @set int,
    @coarea char(4)
as
    with base as (
        select distinct
            a.cheie,
            a.coarea,
            a.data_set,
            a.hier,
            a.cost_centre,
            b.an
        from oxpl.tbl_int_recs_plan_head as a
        inner join oxpl.tbl_int_recs_plan_vals as b
        on a.id = b.head_id
        where a.data_set = @set and a.coarea = @coarea and
            not exists (select * from oxpl.tbl_int_key_vals as m
                        where m.cheie = a.cheie and m.gen_data_set = a.data_set and m.an = b.an and
                            1 = (case when m.cost_centre is null then 1 else iif(m.hier = a.hier and m.cost_centre = a.cost_centre, 1, 0) end))
    )
    select
        b.id,
        (case when b.nume is null then 'fără cheie' else b.nume end) as nume,
        b.coarea,
        b.ktype,
        b.blocat,
        a.data_set,
        a.hier,
        a.cost_centre,
        c.nume as cost_centre_nume,
        cast((select
            n.an,
            cast(1 as float) as valoare
        from base as n
        where (n.cheie = a.cheie or (n.cheie is null and a.cheie is null)) and n.cost_centre = a.cost_centre
        order by n.an asc
        for json path) as nvarchar(max)) as valori
    from (select distinct m.cheie, m.coarea, m.data_set, m.hier, m.cost_centre from base as m) as a

    left join oxpl.tbl_int_key_head as b
    on a.cheie = b.id

    inner join oxpl.tbl_int_ccntr as c
    on a.data_set = c.data_set and a.hier = c.hier and a.cost_centre = c.cod
    order by a.cost_centre asc, b.id asc;