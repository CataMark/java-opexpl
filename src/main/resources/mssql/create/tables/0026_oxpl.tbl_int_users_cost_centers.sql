create table oxpl.tbl_int_users_cost_centers(
    id uniqueidentifier not null constraint tbl_int_users_cost_centers_df1 default newsequentialid(),
    uname varchar(255) not null,
    hier char(5) not null,
    cost_centre varchar(10) not null,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_users_cost_centers_df2 default current_timestamp,
    constraint tbl_int_users_cost_centers_pk primary key (id),
    constraint tbl_int_users_cost_centers_uq1 unique (uname, hier, cost_centre),
    constraint tbl_int_users_cost_centers_fk1 foreign key (hier) references oxpl.tbl_int_coarea(cc_hier)
);