create or alter procedure oxpl.prc_user_group_get_all
as
    select *
    from oxpl.tbl_int_ugrup
    order by ordine asc, cod asc;