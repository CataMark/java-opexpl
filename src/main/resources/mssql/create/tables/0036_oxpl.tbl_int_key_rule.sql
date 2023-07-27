create table oxpl.tbl_int_key_rule(
    cheie int not null,
    medie_pond bit not null constraint tbl_int_key_rule_df1 default 0,
    chei_json nvarchar(max) not null,
    cost_centre_json nvarchar(max),
    opex_categ_json nvarchar(max),
    ic_part_json nvarchar(max),
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_key_rule_df2 default current_timestamp,
    constraint tbl_int_key_rule_pk primary key (cheie),
    constraint tbl_int_key_rule_fk1 foreign key (cheie) references oxpl.tbl_int_key_head(id)
);