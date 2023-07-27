create or alter procedure oxpl.prc_cheie_tip_get_list_all
as
    select * from oxpl.tbl_int_key_type order by cod asc;