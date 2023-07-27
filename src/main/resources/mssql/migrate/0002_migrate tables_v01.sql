/* 0002 */
insert into oxpl.tbl_int_coarea (cod, nume, acronim, alocare, cc_hier, mod_de, mod_timp)
select a.COD, a.NUME, a.ACRONIM, a.ALOCARE, a.CC_HIER, a.MOD_DE, a.MOD_TIMP
from FINSYS.oxpl.tbl_int_coarea as a;
/* 0003 */
insert into oxpl.tbl_int_ic_part (cod, nume, coarea, mod_de, mod_timp)
select a.COD, a.NUME, a.COAREA, a.MOD_DE, a.MOD_TIMP
from FINSYS.dbo.OXPL_INT_IC_PART as a;
/* 0004 */
insert into oxpl.tbl_int_seg_ind (cod, nume, mod_de, mod_timp)
select a.COD, a.NUME, a.MOD_DE, a.MOD_TIMP
from FINSYS.dbo.OXPL_INT_SEG_IND as a;
/* 0005 */
insert into oxpl.tbl_int_buss_line (cod, seg_ind, nume, mod_de, mod_timp)
select a.COD, a.SEG_IND, a.NUME, a.MOD_DE, a.MOD_TIMP
from FINSYS.dbo.OXPL_INT_BUSS_LINE as a;
/* 0006 */
insert into oxpl.tbl_int_buss_line_asg (coarea, buss_line, mod_de, mod_timp)
select a.COAREA, a.BUSS_LINE, a.MOD_DE, a.MOD_TIMP
from FINSYS.dbo.OXPL_INT_BUSS_LINE_ASG as a;
/* 0007 */
insert into oxpl.tbl_int_month (cod, nume, mod_de, mod_timp)
select a.COD, a.NUME, a.MOD_DE, a.MOD_TIMP
from FINSYS.dbo.OXPL_INT_MONTH as a;
/* 0008 */
insert into oxpl.tbl_int_plan_vers (cod, nume, actual, mod_de, mod_timp)
select a.COD, a.NUME, a.ACTUAL, a.MOD_DE, a.MOD_TIMP
from FINSYS.dbo.OXPL_INT_PLAN_VERS as a;
/* 0010 */
set identity_insert oxpl.tbl_int_data_set on;
insert into oxpl.tbl_int_data_set (id, nume, vers, an, impl_compare, blocat, incheiat, mod_de, mod_timp)
select a.ID, a.NUME, a.VERS, a.AN, a.IMPL_COMPARE, a.BLOCAT, a.INCHEIAT, a.MOD_DE, a.MOD_TIMP
from FINSYS.dbo.OXPL_INT_DATA_SET as a;
set identity_insert oxpl.tbl_int_data_set off;
/* 0012 */
insert into oxpl.tbl_int_data_set_per (data_set, an, per, actual, mod_de, mod_timp)
select a.DATA_SET, a.AN, a.PER, a.ACTUAL, a.MOD_DE, a.MOD_TIMP
from FINSYS.dbo.OXPL_INT_DATA_SET_PER as a;
/* 0014 */
begin
    set nocount on;

    declare crs_old_table cursor for
        select ID from FINSYS.dbo.OXPL_INT_CCNTR_GRUP;

    declare @vid int;

    drop table if exists #id_match;
    create table #id_match(
        nid uniqueidentifier,
        vid int
    );

    open crs_old_table;
    fetch next from crs_old_table into @vid;

    while @@fetch_status = 0
    begin
        insert into oxpl.tbl_int_ccntr_grup(hier, data_set, cod, nume, mod_de, mod_timp)
        output inserted.id, @vid into #id_match
        select a.HIER, a.DATA_SET, a.COD, a.NUME, a.MOD_DE, a.MOD_TIMP
        from FINSYS.dbo.OXPL_INT_CCNTR_GRUP as a where a.ID = @vid;

        fetch next from crs_old_table into @vid;
    end

    close crs_old_table;
    deallocate crs_old_table;

    update a
    set a.superior = d.nid
    from oxpl.tbl_int_ccntr_grup as a
    inner join #id_match as b
    on a.id = b.nid
    inner join FINSYS.dbo.OXPL_INT_CCNTR_GRUP as c
    on b.vid = c.id
    inner join #id_match as d
    on c.superior = d.vid;

    drop table #id_match;
    set nocount off;

    select count(*) as pozitii from oxpl.tbl_int_ccntr_grup;
end;
/* 0015 */
insert into oxpl.tbl_int_ccntr (hier, data_set, cod, nume, grup, blocat, mod_de, mod_timp)
select a.HIER, a.DATA_SET, a.COD, a.NUME, a.GRUP, a.BLOCAT, a.MOD_DE, a.MOD_TIMP
from FINSYS.dbo.OXPL_INT_CCNTR as a;
/* 0018 */
insert into oxpl.tbl_int_cost_driver (cod, nume, central, mod_de, mod_timp)
select a.COD, a.NUME, a.CENTRAL, a.MOD_DE, a.MOD_TIMP
from FINSYS.dbo.OXPL_INT_COST_DRIVER as a;
/* 0019 */
insert into oxpl.tbl_int_cost_driver_assign (coarea, cost_driver, blocat, mod_de, mod_timp)
select distinct
    x.COAREA, y.COST_DRIVER, cast(0 as bit) as blocat, 'C12153', current_timestamp
from
    (select distinct a.COAREA, a.OPEX_CATEG
    from FINSYS.dbo.OXPL_INT_RECS_ACT as a
    union
    select distinct a.COAREA, a.OPEX_CATEG
    from FINSYS.dbo.OXPL_INT_RECS_PLAN_HEAD as a) as x
inner join FINSYS.dbo.OXPL_INT_OPEX_CATEG as y
on x.OPEX_CATEG = Y.COD;
/* 0021 */
insert into oxpl.tbl_int_opex_categ (cod, nume, cost_driver, cont_ccoa, mod_de, mod_timp)
select a.COD, a.NUME, a.COST_DRIVER, a.CONT_CCOA, a.MOD_DE, a.MOD_TIMP
from FINSYS.dbo.OXPL_INT_OPEX_CATEG as a;
/* 0022 */
insert into oxpl.tbl_int_opex_categ_assign (coarea, opex_categ, blocat, mod_de, mod_timp)
select distinct
    x.COAREA, x.OPEX_CATEG, cast(0 as bit) as blocat, 'C12153', current_timestamp
from
    (select distinct a.COAREA, a.OPEX_CATEG
    from FINSYS.dbo.OXPL_INT_RECS_ACT as a
    union
    select distinct a.COAREA, a.OPEX_CATEG
    from FINSYS.dbo.OXPL_INT_RECS_PLAN_HEAD as a) as x;
/* 0025 */
insert into oxpl.tbl_int_ugrup (cod, nume, ordine, cost_center_bound, cost_driver_bound, implicit, mod_de, mod_timp)
select a.COD, a.NUME, a.ORDINE, a.COST_CENTER_BOUND, a.COST_DRIVER_BOUND, a.IMPLICIT, a.MOD_DE, a.MOD_TIMP
from FINSYS.dbo.OXPL_INT_UGRUP as a;
/* 0026 */
insert into oxpl.tbl_int_users_cost_centers (uname, hier, cost_centre, mod_de, mod_timp)
select a.UNAME, a.HIER, a.COST_CENTRE, a.MOD_DE, a.MOD_TIMP
from FINSYS.dbo.OXPL_INT_USERS_COST_CENTERS as a;
/* 0028 */
insert into oxpl.tbl_int_users_cost_drivers (uname, coarea, cost_driver, mod_de, mod_timp)
select a.UNAME, a.COAREA, a.COST_DRIVER, a.MOD_DE, a.MOD_TIMP
from FINSYS.dbo.OXPL_INT_USERS_COST_DRIVERS as a;
/* 0030 */
insert into oxpl.tbl_int_key_type(cod, nume, general, calculat, mod_de, mod_timp)
select 'G01', N'Cheie generală cu valori specifice setului de date si centrului de cost', 1, 0, 'C12153', current_timestamp
union
select 'S01', N'Cheie specifică setului de date şi centrului de cost', 0, 0, 'C12153', current_timestamp
union
select 'G02', N'Cheie generală cu un singur şir de valori pe linii de afaceri, specifice setului de date', 1, 0, 'C12153', current_timestamp
union
select 'C01', N'Cheie calculată în baza altor chei generale', 1, 1, 'C12153', current_timestamp;
/* 0032 */
insert into oxpl.tbl_int_key_head (id, nume, descr, coarea, ktype, blocat, data_set, hier, cost_centre, mod_de, mod_timp)
select
    a.ID,
    a.NUME,
    a.DESCR,
    a.COAREA,
    (case a.GENERAL when 1 then 'G01' else 'S01' end) as ktype,
    cast(0 as bit) as blocat,
    a.DATA_SET,
    a.HIER,
    a.COST_CENTRE,
    a.MOD_DE,
    a.MOD_TIMP
from FINSYS.dbo.OXPL_INT_KEY_HEAD as a;
/* 0034 */
insert into oxpl.tbl_int_key_vals (cheie, coarea, buss_line, hier, data_set, cost_centre, gen_data_set, an, valoare, mod_de, mod_timp)
select
    a.CHEIE,
    a.COAREA,
    a.BUSS_LINE,
    a.HIER,
    a.DATA_SET,
    a.COST_CENTRE,
    a.DATA_SET as gen_data_set,
    b.AN,
    a.VALOARE,
    a.MOD_DE,
    a.MOD_TIMP
from FINSYS.dbo.OXPL_INT_KEY_VALS as a
inner join (select distinct DATA_SET, AN from FINSYS.dbo.OXPL_INT_DATA_SET_PER) as b
on a.DATA_SET = b.DATA_SET
ORDER BY a.DATA_SET asc, a.COAREA asc, a.CHEIE asc, A.BUSS_LINE asc, b.AN asc;

/* 0036 oxpl.tbl_int_key_rule nu exista in versiunea 1 */

/* 0038 */
insert into oxpl.tbl_int_recs_act (data_set, an, per, coarea, cost_cntr, cost_cntr_nume, cont_ccoa, cont_ccoa_nume,
                                opex_categ, text_antet, text_nume, obj_part, obj_part_nume, doc_nr,
                                doc_poz, furnizor, part_ic, oper_ref, tranz_afac, tranz_orig, debit_credit,
                                valoare, cantitate, umas, data_creat, mod_de, mod_timp)
select a.DATA_SET, a.AN, a.PER, a.COAREA, a.COST_CNTR, a.COST_CNTR_NUME, a.CONT_CCOA, a.CONT_CCOA_NUME,
        a.OPEX_CATEG, a.TEXT_ANTET, a.TEXT_NUME, a.OBJ_PART, a.OBJ_PART_NUME, a.DOC_NR,
        a.DOC_POZ, a.FURNIZOR, a.PART_IC, a.OPER_REF, a.TRANZ_AFAC, a.TRANZ_ORIG, a.DEBIT_CREDIT,
        a.VALOARE, a.CANTITATE, a.UMAS, a.DATA_CREAT, a.MOD_DE, a.MOD_TIMP
from FINSYS.dbo.OXPL_INT_RECS_ACT as a;
/* 0040 */
insert into oxpl.tbl_int_recs_plan_head ([uid], coarea, descr, hier, data_set, cost_centre, cheie,
                                        opex_categ, ic_part, mod_de, mod_timp)
select a.ID, a.COAREA, a.DESCR, a.HIER, a.DATA_SET, a.COST_CENTRE, a.CHEIE, a.OPEX_CATEG,
        a.IC_PART, a.MOD_DE, a.MOD_TIMP
from FINSYS.dbo.OXPL_INT_RECS_PLAN_HEAD as a;
/* 0042 */
begin
    insert into oxpl.tbl_int_recs_plan_vals (head_id, cont, data_set, an, per, valoare, mod_de, mod_timp)
    select b.id, c.CONT_CCOA, a.DATA_SET, a.an, a.PER, a.VALOARE, a.MOD_DE, a.MOD_TIMP
    from FINSYS.dbo.OXPL_INT_RECS_PLAN_VALS as a

    inner join oxpl.tbl_int_recs_plan_head as b
    on a.HEAD_ID = b.[uid]

    left join FINSYS.dbo.OXPL_INT_OPEX_CATEG as c
    on b.opex_categ = c.COD;

    set nocount on;
    update oxpl.tbl_int_recs_plan_head set [uid] = null;
    set nocount off;
end;
/* 0044 */
insert into oxpl.tbl_int_ccntr_map (hier, data_set, receiver, sender, mod_de, mod_timp)
select a.HIER, a.DATA_SET, a.RECEIVER, a.SENDER, a.MOD_DE, a.MOD_TIMP
from FINSYS.dbo.OXPL_INT_CCNTR_MAP as a
where a.SENDER != a.RECEIVER;