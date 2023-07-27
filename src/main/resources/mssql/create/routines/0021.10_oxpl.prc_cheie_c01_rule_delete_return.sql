create or alter procedure oxpl.prc_cheie_c01_rule_delete_return
    @cheie int
as
    begin
        set nocount on;
        delete from oxpl.tbl_int_key_rule where cheie = @cheie;

        if exists (select * from oxpl.tbl_int_key_rule where cheie = @cheie)
            select cast(0 as bit) as rezultat;
        else
            select cast(1 as bit) as rezultat;
    end;