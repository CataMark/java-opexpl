create table oxpl.tbl_int_recs_plan_vals(
    id uniqueidentifier not null constraint tbl_int_recs_plan_vals_df1 default newsequentialid(),
    head_id uniqueidentifier not null,
    cont char(10),
    data_set int not null,
    an smallint not null,
    per char(2) not null,
    valoare float not null,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_recs_plan_vals_df2 default current_timestamp,
    constraint tbl_int_recs_plan_vals_pk primary key (id),
    constraint tbl_int_recs_plan_vals_uq1 unique (head_id, cont, an, per),
    constraint tbl_int_recs_plan_vals_fk1 foreign key (head_id) references oxpl.tbl_int_recs_plan_head(id),
    constraint tbl_int_recs_plan_vals_fk2 foreign key (data_set, an, per) references oxpl.tbl_int_data_set_per(data_set, an, per),
    constraint tbl_int_recs_plan_vals_ck1 check (valoare != 0)
);
go

create index tbl_int_recs_plan_vals_ix1 on oxpl.tbl_int_recs_plan_vals(data_set, head_id);
go