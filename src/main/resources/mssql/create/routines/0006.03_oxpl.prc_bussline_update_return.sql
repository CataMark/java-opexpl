create or alter procedure oxpl.prc_bussline_update_return
    @cod char(4),
    @seg char(2),
    @nume nvarchar(100),
    @kid varchar(20)
as
    begin
        set nocount on;

        update oxpl.tbl_int_buss_line set
        seg_ind = @seg, nume = @nume, mod_de = @kid, mod_timp = current_timestamp
        where cod = @cod;

        select * from oxpl.fnc_bussline_get_by_cod(@cod);
    end;