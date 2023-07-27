create or alter procedure oxpl.prc_bussline_insert_return
    @cod char(4),
    @seg char(2),
    @nume nvarchar(100),
    @kid varchar(20)
as
    begin
        set nocount on;

        insert into oxpl.tbl_int_buss_line (cod, seg_ind, nume, mod_de, mod_timp)
        values (@cod, @seg, @nume, @kid, current_timestamp);

        select * from oxpl.fnc_bussline_get_by_cod(@cod);
    end;