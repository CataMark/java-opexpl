begin
    declare @kid varchar(20) = ?;
    exec sys.sp_set_session_context @key = N'oxpl_user_id', @value = @kid, @readonly = 0;

    update inrg
    set %s, inrg.mod_de = @kid, inrg.mod_timp = current_timestamp
    from oxpl.tbl_int_recs_act as inrg
    where inrg.id = ?;
end;