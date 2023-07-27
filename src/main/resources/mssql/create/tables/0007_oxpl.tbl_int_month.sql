create table oxpl.tbl_int_month(
    cod char(2) not null,
    nume nvarchar(50) not null,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_month_df1 default current_timestamp,
    constraint tbl_int_month_pk primary key (cod)
);