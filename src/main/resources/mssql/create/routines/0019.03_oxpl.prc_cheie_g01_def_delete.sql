create or alter procedure oxpl.prc_cheie_g01_def_delete
    @id int
as
    delete from oxpl.tbl_int_key_head where id = @id;