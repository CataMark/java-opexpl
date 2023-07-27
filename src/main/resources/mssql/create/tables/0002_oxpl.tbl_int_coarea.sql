create table oxpl.tbl_int_coarea(
    cod char(4) not null,
    nume nvarchar(100) not null,
    acronim varchar(5) not null,
    alocare bit not null constraint tbl_int_coarea_df1 default 0,
    cc_hier char(5) not null,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_coarea_df2 default current_timestamp,
    constraint tbl_int_coarea_pk primary key (cod),
    constraint tbl_int_coarea_uq1 unique (acronim),
    constraint tbl_int_coarea_uq2 unique (cc_hier)
);