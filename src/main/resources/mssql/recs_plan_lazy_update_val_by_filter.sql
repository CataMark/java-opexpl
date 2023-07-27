begin
    declare @kid varchar(20) = ?;
    exec sys.sp_set_session_context @key = N'oxpl_user_id', @value = @kid, @readonly = 0;

    update val
    set %1$s, val.mod_de = @kid, val.mod_timp = current_timestamp
    from oxpl.tbl_int_recs_plan_vals as val
    inner join
        (select
            v.id
        from oxpl.tbl_int_recs_plan_head as q

        inner join oxpl.tbl_int_ccntr as p
        on q.cost_centre = p.cod and q.data_set = p.data_set and q.hier = p.hier

        left join oxpl.tbl_int_key_head as r
        on q.cheie = r.id

        inner join oxpl.tbl_int_opex_categ as s
        on q.opex_categ = s.cod

        inner join oxpl.tbl_int_cost_driver as t
        on s.cost_driver = t.cod

        left join oxpl.tbl_int_ic_part as u
        on q.ic_part = u.cod

        left join oxpl.tbl_int_recs_plan_vals as v
        on q.id = v.head_id

        where q.data_set = ? and q.coarea = ? %2$s) as flt
    on val.id = flt.id;
end;