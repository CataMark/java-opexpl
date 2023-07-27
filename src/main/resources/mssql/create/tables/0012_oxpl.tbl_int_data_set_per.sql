create table oxpl.tbl_int_data_set_per(
    id uniqueidentifier not null constraint tbl_int_data_set_per_df1 default newsequentialid(),
    data_set int not null,
    an smallint not null,
    per char(2) not null,
    actual bit not null constraint tbl_int_data_set_per_df2 default 0,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_data_set_per_df3 default current_timestamp,
    constraint tbl_int_data_set_per_pk primary key (id),
    constraint tbl_int_data_set_per_uq1 unique (data_set, an, per),
    constraint tbl_int_data_set_per_fk1 foreign key (data_set) references oxpl.tbl_int_data_set(id),
    constraint tbl_int_data_set_per_fk2 foreign key (per) references oxpl.tbl_int_month(cod),
    constraint tbl_int_data_set_per_ck1 check (an between 1900 and 9999)
);