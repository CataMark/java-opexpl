create or alter procedure oxpl.prc_costdriver_get_list_by_central
    @central bit
as
    select * from oxpl.tbl_int_cost_driver where central = @central order by cod asc;