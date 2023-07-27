create table oxpl.tbl_int_backup(
    id uniqueidentifier not null constraint tbl_int_backup_df1 default newsequentialid(),
    tabela varchar(100) not null,
    data_set int,
    coarea char(4),
    hier char(5),
    row_id varchar(100) not null,
    json_record nvarchar(max) not null,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_backup_df2 default current_timestamp,
    constraint tbl_int_backup_pk primary key (id)
);
go

create index tbl_int_backup_ix1 on oxpl.tbl_int_backup(tabela, data_set, coarea, hier);
go