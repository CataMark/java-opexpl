begin
    set nocount on;
    declare @kid varchar(20) = ?;
    declare @set int = ?;
    declare @coarea char(4) = ?;

    if exists (select * from oxpl.tbl_int_recs_plan_head as a
                inner join oxpl.tbl_int_recs_plan_vals as b
                on a.id = b.head_id
                inner join oxpl.tbl_int_data_set_per as c
                on b.data_set = c.data_set and b.an = c.an and b.per = c.per
                where a.data_set = @set and a.coarea = @coarea and c.actual = 0)
        begin
            exec sys.sp_set_session_context @key = N'oxpl_not_backup', @value = 0, @readonly = 0;
            exec sys.sp_set_session_context @key = N'oxpl_user_id', @value = @kid, @readonly = 0;
        end;
    else
        exec sys.sp_set_session_context @key = N'oxpl_not_backup', @value = 1, @readonly = 0;


    drop table if exists #oxpl_recs_plan_head_id;
    create table #oxpl_recs_plan_head_id(
        id uniqueidentifier not null
    );

    insert into #oxpl_recs_plan_head_id (id)
    select
        q.id
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

    where q.data_set = @set and q.coarea = @coarea %s ;

    set nocount off;

    begin try
        begin transaction
            delete a
            from oxpl.tbl_int_recs_plan_vals as a
            inner join #oxpl_recs_plan_head_id as b
            on a.head_id = b.id;

            delete a
            from oxpl.tbl_int_recs_plan_head as a
            inner join #oxpl_recs_plan_head_id as b
            on a.id = b.id;
        commit transaction
    end try
    begin catch
        if @@trancount > 0 rollback transaction;
        throw;
    end catch
end;