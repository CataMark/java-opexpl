create or alter procedure oxpl.prc_cheie_c01_rule_get_by_key
    @cheie int
as
    select * from oxpl.tbl_int_key_rule where cheie = @cheie;