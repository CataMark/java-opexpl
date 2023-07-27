create table oxpl.tbl_int_plan_vers(
    cod char(3) not null,
    nume nvarchar(50) not null,
    actual bit not null constraint tbl_int_plan_vers_df1 default 0,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_plan_vers_df2 default current_timestamp,
    constraint tbl_int_plan_vers_pk primary key (cod)
);