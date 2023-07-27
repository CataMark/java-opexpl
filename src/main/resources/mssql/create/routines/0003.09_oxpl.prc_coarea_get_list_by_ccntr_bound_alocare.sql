create or alter procedure oxpl.prc_coarea_get_list_by_ccntr_bound_alocare
    @uname varchar(20)
as
    select a.*
    from oxpl.tbl_int_coarea as a
    inner join (select distinct hier
                from oxpl.tbl_int_users_cost_centers as b
                where uname = @uname) as b
    on a.cc_hier = b.hier
    where a.alocare = 1
    order by a.cod asc;