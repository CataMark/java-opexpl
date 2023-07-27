create or alter procedure oxpl.prc_user_get_list_by_group
    @group varchar(255)
as
    select a.uname, a.nume, a.prenume, a.email, b.mod_de, b.mod_timp
    from portal.tbl_int_users as a
    inner join portal.tbl_int_ugroups as b
    on a.uname = b.uname
    where b.ugroup = @group
    order by a.uname asc;