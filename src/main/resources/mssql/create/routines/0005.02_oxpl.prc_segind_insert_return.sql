create or alter procedure oxpl.prc_segind_insert_return
    @cod char(2),
    @nume nvarchar(100),
    @kid varchar(20)
as
    begin
        set nocount on;

        insert into oxpl.tbl_int_seg_ind (cod, nume, mod_de, mod_timp)
        values (@cod, @nume, @kid, current_timestamp);

        select * from oxpl.fnc_segind_get_by_cod(@cod);
    end;