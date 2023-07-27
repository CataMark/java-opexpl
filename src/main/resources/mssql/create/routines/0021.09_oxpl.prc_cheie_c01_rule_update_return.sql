create or alter procedure oxpl.prc_cheie_c01_rule_update_return
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

    update oxpl.tbl_int_key_rule
    set medie_pond = @medie_pond, chei_json = @chei_json, cost_centre_json = @cost_centre_json, opex_categ_json = @opex_categ_json,
        ic_part_json = @ic_part_json, mod_de = @kid, mod_timp = current_timestamp
    where cheie = @cheie;

    select * from oxpl.tbl_int_key_rule where cheie = @cheie;
end;