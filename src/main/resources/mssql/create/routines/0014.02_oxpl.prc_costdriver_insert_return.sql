create or alter procedure oxpl.prc_costdriver_insert_return
    @cod char(5),
    @nume nvarchar(50),
    @central bit,
    @kid varchar(20)
as
    begin
        set nocount on;

        insert into oxpl.tbl_int_cost_driver (cod, nume, central, mod_de, mod_timp)
        values (@cod, @nume, @central, @kid, current_timestamp);

        select * from oxpl.fnc_costdriver_get_by_cod(@cod);
    end;