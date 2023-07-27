create or alter procedure oxpl.prc_user_has_rights_on_ccenter
    @hier char(5),
    @set int,
    @ccenter varchar(10),
    @kid varchar(20)
as
    if exists (select * from oxpl.fnc_hier_get_childs(@hier, @set, @ccenter, @kid))
        select cast(1 as bit) as rezultat;
    else
        select cast(0 as bit) as rezultat;