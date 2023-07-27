create table oxpl.tbl_int_users_cost_drivers(
    id uniqueidentifier not null constraint tbl_int_users_cost_drivers_df1 default newsequentialid(),
    uname varchar(255) not null,
    coarea char(4) not null,
    cost_driver char(5) not null,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_users_cost_drivers_df2 default current_timestamp,
    constraint tbl_int_users_cost_drivers_pk primary key (id),
    constraint tbl_int_users_cost_drivers_uq1 unique (uname, coarea, cost_driver),
    constraint tbl_int_users_cost_drivers_fk1 foreign key (coarea) references oxpl.tbl_int_coarea(cod)
);