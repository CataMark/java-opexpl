create or alter procedure oxpl.prc_coarea_insert_return
    @cod char(4),
    @nume nvarchar(100),
    @acronim varchar(5),
    @alocare bit,
    @hier char(5),
    @kid varchar(20)
as
    begin
        set nocount on;

        insert into oxpl.tbl_int_coarea (cod, nume, acronim, alocare, cc_hier, mod_de, mod_timp)
        values (@cod, @nume, @acronim, @alocare, @hier, @kid, current_timestamp);

        select * from oxpl.fnc_coarea_get_by_cod(@cod);
    end;