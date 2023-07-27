with a as ( %1$s )
select top %2$s * from a where a.c_rand > ?
order by a.c_rand asc;