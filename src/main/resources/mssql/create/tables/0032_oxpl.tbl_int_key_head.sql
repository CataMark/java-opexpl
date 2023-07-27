create table oxpl.tbl_int_key_head(
    id int not null constraint tbl_int_key_head_df1 default (next value for oxpl.tbl_int_key_head_sq1),
    nume nvarchar(50) not null,
    descr nvarchar(4000) not null,
    coarea char(4) not null,
    ktype char(3) not null,
    blocat bit not null constraint tbl_int_key_head_df2 default 0,
    data_set int,
    hier char(5),
    cost_centre varchar(10),
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_key_head_df3 default current_timestamp,
    constraint tbl_int_key_head_pk primary key (id),
    constraint tbl_int_key_head_fk1 foreign key (coarea) references oxpl.tbl_int_coarea(cod),
    constraint tbl_int_key_head_fk2 foreign key (hier, data_set, cost_centre) references oxpl.tbl_int_ccntr(hier, data_set, cod),
    constraint tbl_int_key_head_fk3 foreign key (ktype) references oxpl.tbl_int_key_type(cod)
);