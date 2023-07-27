select
    q.id as doc_sys_id,
    q.uid,
    q.descr,
    q.cost_centre,
    p.nume as cost_centre_nume,
    q.cheie,
    r.nume as cheie_nume,
    s.cost_driver,
    t.nume as cost_driver_nume,
    q.opex_categ,
    s.nume as opex_categ_nume,
    q.ic_part,
    u.nume as ic_part_nume,
    q.mod_de as doc_mod_de,
    q.mod_timp as doc_mod_timp,
    v.id as val_sys_id,
    v.cont,
    v.an,
    v.per,
    v.valoare,
    v.mod_de as val_mod_de,
    v.mod_timp as val_mod_timp
from oxpl.tbl_int_recs_plan_head as q

inner join oxpl.tbl_int_ccntr as p
on q.cost_centre = p.cod and q.data_set = p.data_set and q.hier = p.hier

left join oxpl.tbl_int_key_head as r
on q.cheie = r.id

inner join oxpl.tbl_int_opex_categ as s
on q.opex_categ = s.cod

inner join oxpl.tbl_int_cost_driver as t
on s.cost_driver = t.cod

left join oxpl.tbl_int_ic_part as u
on q.ic_part = u.cod

left join oxpl.tbl_int_recs_plan_vals as v
on q.id = v.head_id

where q.data_set = ? and q.coarea = ? %s ;