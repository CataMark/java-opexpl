/*begin
    declare @start int =  0;

    --interogare pentru a afla ultimul numar din baza de date FINSYS
    select @start = cast(current_value as int)
    from FINSYS.sys.sequences
    where name = 'OXPL_INT_OPEX_CATEG_SQ1';

    --interogare pentru a afla ultimul numar din baza de date FINSYS1
    select @start = cast(current_value as int)
    from FINSYS1.sys.sequences
    where object_id = object_id('oxpl.tbl_int_opex_categ_sq1');


    declare @sql varchar(max);
    set @sql = 'create sequence oxpl.tbl_int_opex_categ_sq1
                as int
                    start with ' + cast((@start + 1) as varchar) + ' ' +
                    'increment by 1
                    minvalue 10000
                    maxvalue 99999
                    no cycle
                    no cache;';
    exec(@sql);
end;*/

create sequence oxpl.tbl_int_opex_categ_sq1
as int
    start with 10584
    increment by 1
    minvalue 10000
    maxvalue 99999
    no cycle
    no cache;