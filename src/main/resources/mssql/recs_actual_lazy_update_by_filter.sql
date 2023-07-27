begin
    declare @kid varchar(20) = ?;
    exec sys.sp_set_session_context @key = N'oxpl_user_id', @value = @kid, @readonly = 0;
    
    update inrg
    set %1$s, inrg.mod_de = @kid,  inrg.mod_timp = current_timestamp
    from oxpl.tbl_int_recs_act as inrg
    inner join
        (select q.id
        from oxpl.tbl_int_recs_act as q

        inner join oxpl.tbl_int_opex_categ as p
        on q.opex_categ = p.cod

        left join oxpl.tbl_int_ic_part as r
        on q.part_ic = r.cod

        where q.data_set = ? and q.coarea = ? %2$s) as flt
    on inrg.id = flt.id;
end;