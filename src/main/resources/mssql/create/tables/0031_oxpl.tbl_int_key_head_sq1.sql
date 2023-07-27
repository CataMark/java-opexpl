/*begin
    declare @start int =  0;

    --interogare pentru a afla ultimul numar din baza de date FINSYS
    select @start = cast(current_value as int)
    from FINSYS.sys.sequences
    where name = 'OXPL_INT_KEY_HEAD_SQ1';

    --interogare pentru a afla ultimul numar din baza de date FINSYS2
    select @start = cast(current_value as int)
    from FINSYS2.sys.sequences
    where object_id = object_id('oxpl.tbl_int_key_head_sq1');


    declare @sql varchar(max);
    set @sql = 'create sequence oxpl.tbl_int_key_head_sq1
                as int
                    start with ' + cast((@start + 1) as varchar) + ' ' +
                    'increment by 1
                    minvalue 20000000
                    maxvalue 29999999
                    no cycle
                    no cache;';
    exec(@sql);
end;*/

create sequence oxpl.tbl_int_key_head_sq1
as int
    start with 20002437
    increment by 1
    minvalue 20000000
    maxvalue 29999999
    no cycle
    no cache;