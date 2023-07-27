select
    row_number() over (order by %1$s q.id asc) as c_rand,
    q.id,
    q.an,
    q.per,
    q.cost_cntr,
    q.cost_cntr_nume,
    q.cont_ccoa,
    q.cont_ccoa_nume,
    p.cost_driver,
    q.opex_categ,
    p.nume as opex_categ_nume,
    q.text_antet,
    q.text_nume,
    q.obj_part,
    q.obj_part_nume,
    q.doc_nr,
    q.doc_poz,
    q.furnizor,
    q.part_ic,
    r.nume as part_ic_nume,
    q.valoare,
    q.data_creat,
    q.mod_de,
    q.mod_timp
from oxpl.tbl_int_recs_act as q

inner join oxpl.tbl_int_opex_categ as p
on q.opex_categ = p.cod

left join oxpl.tbl_int_ic_part as r
on q.part_ic = r.cod

where q.data_set = ? and q.coarea = ? %2$s