create table oxpl.tbl_int_ugrup(
    cod varchar(255) not null,
    nume nvarchar(255) not null,
    ordine tinyint not null,
    cost_center_bound bit not null constraint tbl_int_ugrup_df1 default 0,
    cost_driver_bound bit not null constraint tbl_int_ugrup_df2 default 0,
    implicit bit not null constraint tbl_int_ugrup_df3 default 0,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_ugrup_df4 default current_timestamp,
    constraint tbl_int_ugrup_pk primary key (cod),
    constraint tbl_int_ugrup_uq1 unique (ordine),
    constraint tbl_int_ugrup_ck1 check (cost_center_bound & cost_driver_bound = 0)
);