create or alter procedure oxpl.prc_cheie_g01_val_get_by_cheie_and_ccenter
    @cheie int,
    @hier char(5),
    @set int,
    @cost_centre varchar(10)
as
    begin

        declare @isGroup bit = 1;
        select top 1 @isGroup = 0 from oxpl.tbl_int_ccntr
        where hier = @hier and data_set = @set and cod = @cost_centre;

        if @isGroup = 0
            select
                a.*
            from oxpl.tbl_int_key_vals as a
            where a.cheie = @cheie and a.hier = @hier and a.data_set = @set and a.cost_centre = @cost_centre;
        else
            select
                a.cheie,
                a.buss_line,
                @cost_centre as cost_centre,
                a.an,
                sum(valoare) as valoare
            from oxpl.tbl_int_key_vals as a
            inner join (select cod from oxpl.fnc_hier_get_childs(@hier, @set, @cost_centre, null)) as b
            on a.cost_centre = b.cod
            where a.cheie = @cheie and a.hier = @hier and a.data_set = @set
            group by a.cheie, a.buss_line, a.an;
    end;