create or alter procedure oxpl.prc_user_check_cost_driver_bound
    @uname varchar(20)
as
    select top 1 a.cost_driver_bound
    from oxpl.tbl_int_ugrup as a
    inner join portal.tbl_int_ugroups as b
    on a.cod = b.ugroup
    where b.uname = @uname and a.cost_center_bound = 0
    order by a.ordine asc;