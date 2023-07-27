create table oxpl.tbl_int_recs_act(
    id uniqueidentifier not null constraint tbl_int_recs_act_df1 default newsequentialid(),
    data_set int not null,
    an smallint not null,
	per char(2) not null,
	coarea char(4) not null,
	cost_cntr varchar(10) not null,
	cost_cntr_nume nvarchar(100),
	cont_ccoa char(10) not null,
	cont_ccoa_nume nvarchar(100),
	opex_categ int not null,
	text_antet nvarchar(1000),
	text_nume nvarchar(4000),
	obj_part varchar(30),
	obj_part_nume nvarchar(50),
	doc_nr varchar(10),
	doc_poz smallint,
	furnizor nvarchar(1000),
	part_ic varchar(5),
	oper_ref varchar(5),
	tranz_afac varchar(5),
	tranz_orig varchar(5),
	debit_credit char(1),
	valoare float not null,
	cantitate float,
	umas varchar(10),
	data_creat date,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_recs_act_df2 default current_timestamp,
    constraint tbl_int_recs_act_pk primary key (id),
    constraint tbl_int_recs_act_fk1 foreign key (data_set, an, per) references oxpl.tbl_int_data_set_per(data_set, an, per),
    constraint tbl_int_recs_act_fk2 foreign key (coarea, opex_categ) references oxpl.tbl_int_opex_categ_assign(coarea, opex_categ),
    constraint tbl_int_recs_act_fk3 foreign key (part_ic) references oxpl.tbl_int_ic_part(cod)
);
go

create index tbl_int_recs_act_ix1 on oxpl.tbl_int_recs_act(data_set, coarea);
go