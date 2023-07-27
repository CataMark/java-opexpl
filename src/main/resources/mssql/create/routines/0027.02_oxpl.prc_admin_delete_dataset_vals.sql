create or alter procedure oxpl.prc_admin_delete_dataset_vals
    @set int,
    @coarea char(4),
    @kid varchar(20)
as
    begin
        set nocount on;
        /* setare variabile de sesiune pentru backup */
        exec sys.sp_set_session_context @key = N'oxpl_user_id', @value = @kid, @readonly = 0;

        begin try
            if exists (select * from oxpl.tbl_int_data_set as a where a.id = @set and a.incheiat = 1)
                raiserror('Setul de date este închis!', 16, 1);

            begin transaction
                /* stergere valori pentru documentele de planificare */
                delete a from oxpl.tbl_int_recs_plan_vals as a
                inner join oxpl.tbl_int_recs_plan_head as b
                on a.head_id = b.id
                where b.coarea = @coarea and b.data_set = @set;

                /* stergere header documente de planificare */
                delete a from oxpl.tbl_int_recs_plan_head as a
                where a.coarea = @coarea and a.data_set = @set;

                /* stergere valori chei de alocare */
                delete a from oxpl.tbl_int_key_vals as a
                where a.coarea = @coarea and a.gen_data_set = @set;

                /* stergere header chei de alocare specifice */
                delete a from oxpl.tbl_int_key_head as a
                where a.coarea = @coarea and a.data_set = @set;

                /* stergere centre de cost */
                delete a from oxpl.tbl_int_ccntr as a
                inner join oxpl.tbl_int_coarea as b
                on a.hier = b.cc_hier
                where b.cod = @coarea and a.data_set = @set;

                /* stergere grupuri de centre de cost */
                delete a from oxpl.tbl_int_ccntr_grup as a
                inner join oxpl.tbl_int_coarea as b
                on a.hier = b.cc_hier
                where b.cod = @coarea and a.data_set = @set;

            commit transaction
        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            throw;
        end catch;
    end;