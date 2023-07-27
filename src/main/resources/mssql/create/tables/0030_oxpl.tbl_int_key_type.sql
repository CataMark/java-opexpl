create table oxpl.tbl_int_key_type(
    cod char(3) not null,
    nume nvarchar(150) not null,
    general bit not null constraint tbl_int_key_type_df1 default 0,
    calculat bit not null constraint tbl_int_key_type_df2 default 0,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_key_type_df3 default current_timestamp,
    constraint tbl_int_key_type_pk primary key (cod)
);