create table oxpl.tbl_int_opex_categ(
    cod int not null constraint tbl_int_opex_categ_df1 default (next value for oxpl.tbl_int_opex_categ_sq1),
    nume nvarchar(50) not null,
    cost_driver char(5) not null,
    cont_ccoa char(10) not null,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_opex_categ_df2 default current_timestamp,
    constraint tbl_int_opex_categ_pk primary key (cod),
    constraint tbl_int_opex_categ_uq1 unique (nume),
    constraint tbl_int_opex_categ_fk1 foreign key (cost_driver) references oxpl.tbl_int_cost_driver(cod)
);