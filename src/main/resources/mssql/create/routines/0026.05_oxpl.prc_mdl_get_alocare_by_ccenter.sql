create or alter procedure oxpl.prc_mdl_get_alocare_by_ccenter
    @set int,
    @coarea char(4),
    @tip varchar(10),
    @ccenter varchar(10)
as
    begin
        set nocount on;

        declare @hier char(5);
        select @hier = a.cc_hier from oxpl.tbl_int_coarea as a where a.cod = @coarea;

        select
            a.*
        from oxpl.tbl_mdl_alocare as a
        inner join oxpl.fnc_hier_get_childs(@hier, @set, @ccenter, null) as b
        on a.cost_centre = b.cod
        where a.data_set = @set and a.coarea = @coarea and a.val_tip = @tip;
    end;