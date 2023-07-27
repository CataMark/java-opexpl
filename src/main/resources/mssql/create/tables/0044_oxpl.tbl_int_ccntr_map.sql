create table oxpl.tbl_int_ccntr_map(
    id uniqueidentifier not null constraint tbl_int_ccntr_map_df1 default newsequentialid(),
    hier char(5) not null,
    data_set int not null,
    receiver varchar(10) not null,
    sender varchar(10) not null,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_ccntr_map_df2 default current_timestamp,
    constraint tbl_int_ccntr_map_pk primary key (id),
    constraint tbl_int_ccntr_map_uq1 unique (hier, data_set, sender),
    constraint tbl_int_ccntr_map_fk1 foreign key (hier, data_set, receiver) references oxpl.tbl_int_ccntr(hier, data_set, cod),
    constraint tbl_int_ccntr_map_ck1 check (sender != receiver)
);