create or alter procedure oxpl.prc_mdl_get_alocare
    @set int,
    @coarea char(4)
as
    select
        a.*
    from oxpl.tbl_mdl_alocare as a
    where a.data_set = @set and a.coarea = @coarea;