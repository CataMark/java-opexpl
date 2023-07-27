create or alter procedure oxpl.prc_user_group_get_specific_list
as
    select *
    from oxpl.tbl_int_ugrup
    where implicit = 0
    order by ordine asc, cod asc;