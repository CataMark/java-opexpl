create table oxpl.tbl_int_ic_part(
    cod varchar(5) not null,
    nume nvarchar(50) not null,
    coarea char(4),
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_ic_part_df1 default current_timestamp,
    constraint tbl_int_ic_part_pk primary key (cod),
    constraint tbl_int_ic_part_fk1 foreign key (coarea) references oxpl.tbl_int_coarea(cod)
);