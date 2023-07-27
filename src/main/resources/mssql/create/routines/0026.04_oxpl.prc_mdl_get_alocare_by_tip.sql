create or alter procedure oxpl.prc_mdl_get_alocare_by_tip
    @set int,
    @coarea char(4),
    @tip varchar(10)
as
    select
        a.*
    from oxpl.tbl_mdl_alocare as a
    where a.data_set = @set and a.coarea = @coarea and a.val_tip = @tip;