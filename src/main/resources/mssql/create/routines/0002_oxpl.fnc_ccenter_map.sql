create or alter function oxpl.fnc_ccenter_map(
    @hier char(5),
    @data_set int
)
returns table
as return
    select a.cod as sender, a.cod as receiver
    from oxpl.tbl_int_ccntr as a
    left join oxpl.tbl_int_ccntr_map as b
    on a.hier = b.hier and a.data_set = b.data_set and a.cod = b.sender
    where a.hier = @hier and a.data_set = @data_set and b.sender is null

    union

    select a.sender, a.receiver
    from oxpl.tbl_int_ccntr_map as a
    where a.hier = @hier and a.data_set = @data_set;