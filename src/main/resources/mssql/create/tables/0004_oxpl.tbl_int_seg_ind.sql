create table oxpl.tbl_int_seg_ind(
    cod char(2) not null,
    nume nvarchar(100) not null,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_seg_ind_df1 default current_timestamp,
    constraint tbl_int_seg_ind_pk primary key (cod)
);