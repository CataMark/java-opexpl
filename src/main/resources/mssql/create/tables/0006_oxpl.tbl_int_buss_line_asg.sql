create table oxpl.tbl_int_buss_line_asg(
    id uniqueidentifier not null constraint tbl_int_buss_line_asg_df1 default newsequentialid(),
    coarea char(4) not null,
    buss_line char(4) not null,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_buss_line_asg_df2 default current_timestamp,
    constraint tbl_int_buss_line_asg_pk primary key (id),
    constraint tbl_int_buss_line_asg_uq1 unique (coarea, buss_line),
    constraint tbl_int_buss_line_asg_fk1 foreign key (coarea) references oxpl.tbl_int_coarea(cod),
    constraint tbl_int_buss_line_asg_fk2 foreign key (buss_line) references oxpl.tbl_int_buss_line(cod)
);