create or alter procedure oxpl.prc_mdl_get_sumar
    @set int,
    @coarea char(4)
as
    select
        (select count(*) from oxpl.tbl_mdl_alocare as a where a.data_set = @set and a.coarea = @coarea and a.val_tip = 'planificat') as poz_plan,
        (select count(*) from oxpl.tbl_mdl_alocare as a where a.data_set = @set and a.coarea = @coarea and a.val_tip = 'alocat') as poz_aloc,
        (select max(a.doc_mod_timp) from oxpl.tbl_mdl_alocare as a where a.data_set = @set and a.coarea = @coarea and a.val_tip = 'planificat') as doc_mod_timp,
        (select max(a.val_mod_timp) from oxpl.tbl_mdl_alocare as a where a.data_set = @set and a.coarea = @coarea and a.val_tip = 'planificat') as val_mod_timp;