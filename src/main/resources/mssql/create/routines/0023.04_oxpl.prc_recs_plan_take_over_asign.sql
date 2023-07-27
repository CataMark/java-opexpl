create or alter procedure oxpl.prc_recs_plan_take_over_asign
    @coarea char(4),
    @from_set int,
    @dest_set int,
    @kid varchar(20)
as
    begin
        set nocount on;
        exec sys.sp_set_session_context @key = N'oxpl_user_id', @value = @kid, @readonly = 0;

        begin try
            if exists (select * from oxpl.tbl_int_recs_plan_head as a
                        where a.data_set = @dest_set and a.coarea = @coarea and a.cheie is not null)
                raiserror('Există deja chei asignate pe înregistrările în scop!', 16, 1);

            /* colectare reguli de asignare din setul de date sursa */
            drop table if exists #oxpl_plan_asign_rules;
            select
                a.cost_centre,
                a.opex_categ,
                a.ic_part,
                a.cheie,
                sum(b.valoare) as valoare
            into #oxpl_plan_asign_rules
            from oxpl.tbl_int_recs_plan_head as a

            inner join oxpl.tbl_int_recs_plan_vals as b
            on a.id = b.head_id

            inner join oxpl.tbl_int_key_head as c
            on a.cheie = c.id

            inner join oxpl.tbl_int_key_type as d
            on c.ktype = d.cod

            where a.data_set = @from_set and a.coarea = @coarea and d.general = 1
            group by a.cost_centre, a.opex_categ, a.ic_part, a.cheie;

            begin transaction
                set nocount off;

                update a
                set a.cheie = b.cheie, a.mod_de = @kid, a.mod_timp = current_timestamp
                from oxpl.tbl_int_recs_plan_head as a
                inner join (select distinct
                                m.cost_centre,
                                m.opex_categ,
                                m.ic_part,
                                first_value(m.cheie) over (partition by m.cost_centre, m.opex_categ, m.ic_part order by m.valoare desc
                                                            rows between unbounded preceding and unbounded following) as cheie
                            from #oxpl_plan_asign_rules as m) as b
                on a.cost_centre = b.cost_centre and a.opex_categ = b.opex_categ and (a.ic_part = b.ic_part or (a.ic_part is null and b.ic_part is null))
                where a.data_set = @dest_set and a.coarea = @coarea;
            commit transaction
        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            throw;
        end catch
    end;