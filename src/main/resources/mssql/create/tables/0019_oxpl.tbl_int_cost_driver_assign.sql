create table oxpl.tbl_int_cost_driver_assign(
    id uniqueidentifier not null constraint tbl_int_cost_driver_assign_df1 default newsequentialid(),
    coarea char(4) not null,
    cost_driver char(5) not null,
    blocat bit not null constraint tbl_int_cost_driver_assign_df2 default 0,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_cost_driver_assign_df3 default current_timestamp,
    constraint tbl_int_cost_driver_assign_pk primary key (id),
    constraint tbl_int_cost_driver_assign_uq1 unique (coarea, cost_driver),
    constraint tbl_int_cost_driver_assign_fk1 foreign key (coarea) references oxpl.tbl_int_coarea(cod),
    constraint tbl_int_cost_driver_assign_fk2 foreign key (cost_driver) references oxpl.tbl_int_cost_driver(cod)
);