create or alter procedure oxpl.prc_coarea_get_list_alocare
as
    select * from oxpl.tbl_int_coarea where alocare = 1;