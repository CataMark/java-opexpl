create or alter procedure oxpl.prc_costcentermap_insert_return
    @hier char(5),
    @set int,
    @receiver varchar(10),
    @sender varchar(10),
    @kid varchar(20)
as
    begin
        set nocount on;
        declare @id table (id uniqueidentifier);

        insert into oxpl.tbl_int_ccntr_map (hier, data_set, receiver, sender, mod_de, mod_timp)
        output inserted.id into @id
        values (@hier, @set, @receiver, @sender, @kid, current_timestamp);

        declare @compare_set int;
        declare @actual_set int;
        declare @receiver_nume nvarchar(100);
        declare @sender_nume nvarchar(100);

        select @compare_set = a.impl_compare, @actual_set = a.actual_set
        from oxpl.tbl_int_data_set as a
        where a.id = @set;

        select @receiver_nume = a.nume
        from oxpl.tbl_int_ccntr as a
        where a.hier = @hier and a.data_set = @set and a.cod = @receiver;

        select @sender_nume = first_value(rsl.nume) over (order by rsl.last_update desc)
        from
            (select a.cod, a.nume, a.mod_timp as last_update
            from oxpl.tbl_int_ccntr as a
            where a.hier = @hier and a.data_set = @compare_set and a.cod = @sender
            
            union
            
            select distinct
                a.cost_cntr as cod,
                first_value(a.cost_cntr_nume) over (partition by a.cost_cntr order by a.data_creat desc rows between unbounded preceding and unbounded following) as nume,
                max(a.data_creat) over (partition by a.cost_cntr) as last_update
            from oxpl.tbl_int_recs_act as a
            inner join (select cod from oxpl.tbl_int_coarea where cc_hier = @hier) as b
            on a.coarea = b.cod
            inner join (select an, per from oxpl.tbl_int_data_set_per where data_set = @set and actual = 1) as c
            on a.an = c.an and a.per = c.per
            where a.data_set = @actual_set and a.cost_cntr = @sender) as rsl    

        select top 1 a.id, a.hier, a.data_set, a.receiver, @receiver_nume as receiver_nume, a.sender, @sender_nume as sender_nume, a.mod_de, a.mod_timp
        from oxpl.tbl_int_ccntr_map as a
        inner join @id as b
        on a.id = b.id;
    end;