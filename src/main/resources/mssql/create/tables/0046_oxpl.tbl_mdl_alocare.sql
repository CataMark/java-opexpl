create table oxpl.tbl_mdl_alocare(
    doc_id uniqueidentifier,
    coarea char(4) not null,
    coarea_nume nvarchar(100),
    coarea_acronim varchar(5),
    hier char(5),
    descr nvarchar(2000),
    data_set int not null,
    data_set_nume nvarchar(30),
    data_set_vers char(3),
    data_set_an smallint,
    cost_centre varchar(10),
    cost_centre_nume nvarchar(100),
    cost_centre_blocat bit,
    cheie int,
    cheie_nume nvarchar(50),
    cheie_descr nvarchar(4000),
    cheie_tip char(3),
    cost_driver char(5),
    cost_driver_nume nvarchar(50),
    cost_driver_central bit,
    opex_categ int,
    opex_categ_nume nvarchar(50),
    ic_part varchar(5),
    ic_part_nume nvarchar(50),
    val_id uniqueidentifier,
    val_tip varchar(10),
    actual bit,
    cont char(10),
    buss_line char(4),
    buss_line_seg char(2),
    buss_line_nume nvarchar(100),
    an smallint,
    per char(2),
    valoare float,
    doc_mod_de varchar(20),
    doc_mod_timp datetime,
    val_mod_de varchar(20),
    val_mod_timp datetime
);
go

create index tbl_mdl_alocare_ix1 on oxpl.tbl_mdl_alocare(data_set, coarea);
go

select count(*) from oxpl.tbl_mdl_alocare where coarea = '1931';