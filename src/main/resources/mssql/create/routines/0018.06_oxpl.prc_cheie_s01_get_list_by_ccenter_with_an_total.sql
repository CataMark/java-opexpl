create or alter procedure oxpl.prc_cheie_s01_get_list_by_ccenter_with_an_total
    @hier char(5),
    @set int,
    @cost_centre varchar(10),
    @kid varchar(20)
as
    select
        a.*,
        b.nume as cost_centre_nume,
        cast((select
            n.an,
            sum(n.valoare) as valoare
        from oxpl.tbl_int_key_vals as n
        where n.cheie = a.id
        group by n.an
        order by n.an asc
        for json path) as nvarchar(max)) as valori
    from oxpl.tbl_int_key_head as a
    inner join (select cod, nume from oxpl.fnc_hier_get_childs(@hier, @set, @cost_centre, @kid)) as b
    on a.cost_centre = b.cod
    where a.ktype = 'S01' and a.hier = @hier and a.data_set = @set
    order by a.cost_centre asc, a.id asc;