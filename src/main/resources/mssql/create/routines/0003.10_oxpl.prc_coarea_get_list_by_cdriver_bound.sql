create or alter procedure oxpl.prc_coarea_get_list_by_cdriver_bound
    @uname varchar(20)
as
    select a.*
    from oxpl.tbl_int_coarea as a
    inner join (select distinct coarea
                from oxpl.tbl_int_users_cost_drivers as b
                where uname = @uname) as b
    on a.cod = b.coarea
    order by a.cod asc;