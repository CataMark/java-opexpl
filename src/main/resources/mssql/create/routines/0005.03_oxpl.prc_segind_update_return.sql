create or alter procedure oxpl.prc_segind_update_return
    @cod char(2),
    @nume nvarchar(100),
    @kid varchar(20)
as
    begin
        set nocount on;

        update oxpl.tbl_int_seg_ind set
        nume = @nume, mod_de = @kid, mod_timp = current_timestamp
        where cod = @cod;

        select * from oxpl.fnc_segind_get_by_cod(@cod);
    end;