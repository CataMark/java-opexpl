create or alter procedure oxpl.prc_cheie_c01_rule_check_val_exists
    @set int,
    @coarea char(4),
    @medie_pond bit,
    @chei_json nvarchar(max),
    @cost_centre_json nvarchar(max),
    @opex_categ_json nvarchar(max),
    @ic_part_json nvarchar(max)
as
    begin
        set nocount on;

        drop table if exists #oxpl_key_criterii;
        create table #oxpl_key_criterii(
            data_set int not null,
            coarea char(4) not null,
            cheie int not null,
            cost_centre varchar(10),
            cost_driver char(5),
            opex_categ int,
            ic_part varchar(5)
        );

        with base as (
            select @set as data_set, @coarea as coarea
        )
        insert into #oxpl_key_criterii (data_set, coarea, cheie, cost_centre, cost_driver, opex_categ, ic_part)
        select
            a.data_set,
            a.coarea,
            b.cheie,
            c.cost_centre,
            d.cost_driver,
            d.opex_categ,
            e.ic_part
        from base as a
        outer apply (select cast(value as int) as cheie from openjson(@chei_json)) as b
        outer apply (select cast(value as varchar(10)) as cost_centre from openjson(@cost_centre_json)) as c
        outer apply (select cost_driver, opex_categ
                    from openjson(@opex_categ_json)
                        with(
                            cost_driver char(5) '$.cost_driver',
                            opex_categ int '$.opex_categ'
                        )) as d
        outer apply (select cast(value as varchar(5)) as ic_part from openjson(@ic_part_json)) as e;

        if @medie_pond = 0
            if (select count(distinct cheie) from #oxpl_key_criterii) != 1
                raiserror('Regula de calcul funizată nu este ok pentru medie aritmetică!', 16, 1);

        declare @rezultat bit = 0;
        if exists (select * from oxpl.tbl_int_key_vals as a
                    inner join #oxpl_key_criterii as b
                    on a.cheie = b.cheie and a.gen_data_set = b.data_set and a.coarea = b.coarea
                    where 1 = (case when b.cost_centre is null or a.cost_centre is null then 1 else iif(a.cost_centre = b.cost_centre, 1, 0) end))
            set @rezultat = 1;

        if @medie_pond = 1
            begin
                declare @ic_part_flag bit = 0;
                if exists (select * from #oxpl_key_criterii where ic_part is not null)
                    set @ic_part_flag = 1;


                if exists (select * from oxpl.tbl_int_recs_plan_head as a

                            inner join oxpl.tbl_int_opex_categ as b
                            on a.opex_categ = b.cod

                            inner join #oxpl_key_criterii as c
                            on a.data_set = c.data_set and a.coarea = c.coarea

                            where c.cheie = a.cheie and
                                1 = (case when c.cost_centre is null then 1 else iif(c.cost_centre = a.cost_centre, 1, 0) end) and
                                1 = (case when c.cost_driver is null then 1 else iif(c.cost_driver = b.cost_driver, 1, 0) end) and
                                1 = (case when c.opex_categ is null then 1 else iif(c.opex_categ = a.opex_categ, 1, 0) end) and
                                1 = (case
                                        when @ic_part_flag = 0 and c.ic_part is null then 1
                                        when @ic_part_flag = 1 and c.ic_part is null and a.ic_part is null then 1
                                        else iif(c.ic_part = a.ic_part, 1, 0) end))
                    set @rezultat = @rezultat & 1;
                else
                    set @rezultat = @rezultat & 0;
            end;

        select @rezultat as rezultat;
    end;