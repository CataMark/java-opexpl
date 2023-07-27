create table oxpl.tbl_int_ccntr(
    id uniqueidentifier not null constraint tbl_int_ccntr_df1 default newsequentialid(),
    hier char(5) not null,
    data_set int not null,
    cod varchar(10) not null,
    nume nvarchar(100) not null,
    grup varchar(10) not null,
    blocat bit not null constraint tbl_int_ccntr_df2 default 0,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_ccntr_df3 default current_timestamp,
    constraint tbl_int_ccntr_pk primary key (id),
    constraint tbl_int_ccntr_uq1 unique (hier, data_set, cod),
    constraint tbl_int_ccntr_fk1 foreign key (hier, data_set, grup) references oxpl.tbl_int_ccntr_grup(hier, data_set, cod)
);