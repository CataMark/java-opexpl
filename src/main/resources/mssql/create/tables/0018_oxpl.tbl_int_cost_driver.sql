create table oxpl.tbl_int_cost_driver(
    cod char(5) not null,
    nume nvarchar(50) not null,
    central bit not null constraint tbl_int_cost_driver_df1 default 1,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_cost_driver_df2 default current_timestamp,
    constraint tbl_int_cost_driver_pk primary key (cod)
);