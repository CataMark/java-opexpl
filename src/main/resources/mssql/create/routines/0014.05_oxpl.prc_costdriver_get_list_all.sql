create or alter procedure oxpl.prc_costdriver_get_list_all
as
    select * from oxpl.tbl_int_cost_driver order by cod asc;