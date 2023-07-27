create or alter procedure oxpl.prc_costcentermap_get_list_all_mapped
    @hier char(5),
    @set int
as
    begin
        set nocount on;
        
        drop table if exists #oxpl_ccntr_sender;
        create table #oxpl_ccntr_sender(
            cod varchar(10) not null,
            nume nvarchar(100)
        );

        insert into #oxpl_ccntr_sender (cod, nume)
        select distinct
            rsl.cod,
            first_value(rsl.nume) over (partition by rsl.cod order by rsl.last_update desc rows between unbounded preceding and unbounded following) as nume
        from
            (select
                a.cod,
                a.nume,
                a.mod_timp as last_update
            from oxpl.tbl_int_ccntr as a
            inner join oxpl.tbl_int_data_set as b
            on a.data_set = b.impl_compare
            where a.hier = @hier and b.id = @set

            union all

            select distinct
                a.cost_cntr as cod,
                first_value(a.cost_cntr_nume) over (partition by a.cost_cntr order by a.data_creat desc rows between unbounded preceding and unbounded following) as nume,
                cast(max(a.data_creat) over (partition by a.cost_cntr) as datetime) as last_update
            from oxpl.tbl_int_recs_act as a

            inner join oxpl.tbl_int_data_set as b
            on a.data_set = b.actual_set

            inner join oxpl.tbl_int_coarea as c
            on a.coarea = c.cod

            inner join oxpl.tbl_int_data_set_per as d
            on b.id = d.data_set and a.an = d.an and a.per = d.per

            where c.cc_hier = @hier and b.id = @set and d.actual = 1) as rsl;

        select
            row_number() over (order by (select null)) as row_id,
            rsl.id,
            rsl.hier,
            rsl.data_set,
            rsl.receiver,
            rsl.receiver_nume,
            rsl.sender,
            rsl.sender_nume,
            rsl.mod_de,
            rsl.mod_timp
        from
            (select null as id, a.hier, a.data_set, a.cod as receiver, a.nume as receiver_nume, a.cod as sender, a.nume as sender_nume, null as mod_de, null as mod_timp
            from oxpl.tbl_int_ccntr as a
            left join oxpl.tbl_int_ccntr_map as b
            on a.hier = b.hier and a.data_set = b.data_set and a.cod = b.sender
            where a.hier = @hier and a.data_set = @set and b.sender is null

            union

            select a.id, a.hier, a.data_set, a.receiver, c.nume as receiver_nume, a.sender, b.nume as sender_nume, a.mod_de, a.mod_timp
            from oxpl.tbl_int_ccntr_map as a

            inner join #oxpl_ccntr_sender as b
            on a.sender = b.cod

            inner join oxpl.tbl_int_ccntr as c
            on a.hier = c.hier and a.data_set = c.data_set and a.receiver = c.cod

            where c.hier = @hier and c.data_set = @set) as rsl
        order by (case when rsl.id is null then 0 else 1 end) asc, rsl.sender asc, rsl.receiver asc;
    end;