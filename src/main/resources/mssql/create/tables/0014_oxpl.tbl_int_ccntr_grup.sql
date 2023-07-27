create table oxpl.tbl_int_ccntr_grup(
    id uniqueidentifier not null constraint tbl_int_ccntr_grup_df1 default newsequentialid(),
    hier char(5) not null,
    data_set int not null,
    cod varchar(10) not null,
    nume nvarchar(50) not null,
    superior uniqueidentifier,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_ccntr_grup_df2 default current_timestamp,
    constraint tbl_int_ccntr_grup_pk primary key (id),
    constraint tbl_int_ccntr_grup_uq1 unique (hier, data_set, cod),
    constraint tbl_int_ccntr_grup_fk1 foreign key (hier) references oxpl.tbl_int_coarea(cc_hier),
    constraint tbl_int_ccntr_grup_fk2 foreign key (data_set) references oxpl.tbl_int_data_set(id),
    constraint tbl_int_ccntr_grup_fk3 foreign key (superior) references oxpl.tbl_int_ccntr_grup(id),
    constraint tbl_int_ccntr_grup_ck1 check (id != superior)
);