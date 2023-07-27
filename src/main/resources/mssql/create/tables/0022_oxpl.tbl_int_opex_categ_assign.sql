create table oxpl.tbl_int_opex_categ_assign(
    id uniqueidentifier not null constraint tbl_int_opex_categ_assign_df1 default newsequentialid(),
    coarea char(4) not null,
    opex_categ int not null,
    blocat bit not null constraint tbl_int_opex_categ_assign_df2 default 0,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_opex_categ_assign_df3 default current_timestamp,
    constraint tbl_int_opex_categ_assign_pk primary key (id),
    constraint tbl_int_opex_categ_assign_uq1 unique (coarea, opex_categ),
    constraint tbl_int_opex_categ_assign_fk1 foreign key (coarea) references oxpl.tbl_int_coarea(cod),
    constraint tbl_int_opex_categ_assign_fk2 foreign key (opex_categ) references oxpl.tbl_int_opex_categ(cod)
);