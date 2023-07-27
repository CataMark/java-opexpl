create table oxpl.tbl_int_recs_plan_head(
    id uniqueidentifier not null constraint tbl_int_recs_plan_head_df1 default newsequentialid(),
    coarea char(4) not null,
    descr nvarchar(2000) not null,
    hier char(5) not null,
    data_set int not null,
    cost_centre varchar(10) not null,
    cheie int,
    opex_categ int not null,
    ic_part varchar(5),
    [uid] varchar(30),
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_recs_plan_head_df2 default current_timestamp,
    constraint tbl_int_recs_plan_head_pk primary key (id),
    constraint tbl_int_recs_plan_head_fk1 foreign key (coarea, opex_categ) references oxpl.tbl_int_opex_categ_assign(coarea, opex_categ),
    constraint tbl_int_recs_plan_head_fk2 foreign key (hier, data_set, cost_centre) references oxpl.tbl_int_ccntr(hier, data_set, cod),
    constraint tbl_int_recs_plan_head_fk3 foreign key (cheie) references oxpl.tbl_int_key_head(id),
    constraint tbl_int_recs_plan_head_fk4 foreign key (ic_part) references oxpl.tbl_int_ic_part(cod)
);
go

create index tbl_int_recs_plan_head_ix1 on oxpl.tbl_int_recs_plan_head(data_set, coarea);
go