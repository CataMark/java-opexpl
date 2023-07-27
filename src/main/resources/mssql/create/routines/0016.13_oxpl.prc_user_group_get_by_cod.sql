create or alter procedure oxpl.prc_user_group_get_by_cod
    @cod varchar(255)
as
    select
        *
    from oxpl.tbl_int_ugrup as a
    where a.cod = @cod;