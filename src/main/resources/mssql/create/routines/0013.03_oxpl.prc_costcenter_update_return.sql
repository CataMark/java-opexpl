create or alter procedure oxpl.prc_costcenter_update_return
    @id uniqueidentifier,
    @nume nvarchar(100),
    @grup varchar(10),
    @blocat bit,
    @kid varchar(20)
as
    begin
        set nocount on;

        exec sys.sp_set_session_context @key = N'oxpl_user_id', @value = @kid, @readonly = 0;
        update oxpl.tbl_int_ccntr
        set nume = @nume, grup = @grup, blocat = @blocat, mod_de = @kid, mod_timp = current_timestamp
        where id = @id;

        select * from oxpl.fnc_costcenter_get_by_id(@id);
    end;