create or alter procedure oxpl.prc_cheie_c01_rule_insert_return
    @cheie int,
    @medie_pond bit,
    @chei_json nvarchar(max),
    @cost_centre_json nvarchar(max),
    @opex_categ_json nvarchar(max),
    @ic_part_json nvarchar(max),
    @kid varchar(20)
as
    begin
        set nocount on;

        insert into oxpl.tbl_int_key_rule(cheie, medie_pond, chei_json, cost_centre_json, opex_categ_json, ic_part_json, mod_de, mod_timp)
        values(@cheie, @medie_pond, @chei_json, @cost_centre_json, @opex_categ_json, @ic_part_json, @kid, current_timestamp);

        select top 1 a.*
        from oxpl.tbl_int_key_rule as a
        where a.cheie = @cheie;
    end;