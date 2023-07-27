create or alter procedure oxpl.prc_costcentermap_update_return
    @id uniqueidentifier,
    @receiver varchar(10),
    @sender varchar(10),
    @kid varchar(20)
as
    begin
        set nocount on;
        declare @record_data table (hier char(5), data_set int);

        update a
        set a.receiver = @receiver, a.mod_de = @kid, a.mod_timp = current_timestamp
        output inserted.hier, inserted.data_set into @record_data(hier, data_set)
        from oxpl.tbl_int_ccntr_map as a
        where a.id = @id;

        declare @compare_set int;
        declare @actual_set int;
        declare @receiver_nume nvarchar(100);
        declare @sender_nume nvarchar(100);

        select top 1 @compare_set = a.impl_compare, @actual_set = a.actual_set
        from oxpl.tbl_int_data_set as a
        inner join @record_data as b
        on a.id = b.data_set;

        select top 1 @receiver_nume =  a.nume
        from oxpl.tbl_int_ccntr as a
        inner join @record_data as b
        on a.hier = b.hier and a.data_set = b.data_set
        where a.cod = @receiver;

        select @sender_nume = first_value(rsl.nume) over (order by rsl.last_update desc)
        from
            (select a.cod, a.nume, a.mod_timp as last_update
            from oxpl.tbl_int_ccntr as a
            inner join @record_data as b
            on a.hier = b.hier
            where a.data_set = @compare_set and a.cod = @sender
            
            union
            
            select distinct
                a.cost_cntr as cod,
                first_value(a.cost_cntr_nume) over (partition by a.cost_cntr order by a.data_creat desc rows between unbounded preceding and unbounded following) as nume,
                max(a.data_creat) over (partition by a.cost_cntr) as last_update
            from oxpl.tbl_int_recs_act as a
            inner join (select m.cod from oxpl.tbl_int_coarea as m
                        inner join @record_data as n
                        on m.cc_hier = n.hier) as b
            on a.coarea = b.cod
            inner join (select q.an, q.per from oxpl.tbl_int_data_set_per as q
                        inner join @record_data as p
                        on q.data_set = p.data_set
                        where q.actual = 1) as c
            on a.an = c.an and a.per = c.per
            where a.data_set = @actual_set and a.cost_cntr = @sender) as rsl


        select top 1 a.id, a.hier, a.data_set, a.receiver, @receiver_nume as receiver_nume, a.sender, @sender_nume as sender_nume, a.mod_de, a.mod_timp
        from oxpl.tbl_int_ccntr_map as a
        where a.id = @id;
    end;