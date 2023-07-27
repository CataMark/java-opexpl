create or alter procedure oxpl.prc_cheie_c01_val_delete_by_coarea_and_set
    @set int,
    @coarea char(4),
    @kid varchar(20)
as
    begin
        set nocount on;

        exec sys.sp_set_session_context @key = N'oxpl_user_id', @value = @kid, @readonly = 0;
        delete a from oxpl.tbl_int_key_vals as a
        inner join oxpl.tbl_int_key_head as b
        on a.cheie = b.id
        where b.ktype = 'C01' and b.coarea = @coarea and a.gen_data_set = @set;
    end;