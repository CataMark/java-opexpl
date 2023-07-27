create or alter procedure oxpl.prc_costcentergroup_update_return
    @id uniqueidentifier,
    @nume nvarchar(50),
    @superior uniqueidentifier,
    @kid varchar(20)
as
    begin
        set nocount on;

        exec sys.sp_set_session_context @key = N'oxpl_user_id', @value = @kid, @readonly = 0;
        update oxpl.tbl_int_ccntr_grup
        set nume = @nume, superior = @superior, mod_de = @kid, mod_timp = current_timestamp
        where id = @id;

        select * from oxpl.fnc_costcentergroup_get_by_id(@id);
    end;