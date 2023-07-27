create or alter procedure oxpl.prc_cheie_g01_val_delete_by_cheie_and_set
    @cheie int,
    @set int,
    @kid varchar(20)
as
    begin
        exec sys.sp_set_session_context @key = N'oxpl_user_id', @value = @kid, @readonly = 0;        
        delete from oxpl.tbl_int_key_vals where cheie = @cheie and gen_data_set = @set;
    end;