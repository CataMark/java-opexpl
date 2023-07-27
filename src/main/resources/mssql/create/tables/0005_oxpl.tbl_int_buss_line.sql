create table oxpl.tbl_int_buss_line(
    cod char(4) not null,
    seg_ind char(2) not null,
    nume nvarchar(100) not null,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_buss_line_df1 default current_timestamp,
    constraint tbl_int_buss_line_pk primary key (cod),
    constraint tbl_int_buss_line_fk1 foreign key (seg_ind) references oxpl.tbl_int_seg_ind(cod)
);