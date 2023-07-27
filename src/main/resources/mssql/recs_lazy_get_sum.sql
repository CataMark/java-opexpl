with a as ( %s )
select coalesce(sum(valoare),0) as suma from a;