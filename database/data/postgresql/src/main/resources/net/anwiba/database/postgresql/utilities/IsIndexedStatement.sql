select count(*)
  from pg_index ki,
       pg_namespace s,
       pg_class t,
       pg_attribute a
 where ki.indrelid = t.oid
   and s.nspname = ?
   and s.oid = t.relnamespace
   and t.relname = ?
   and t.oid = a.attrelid
   and a.attname = ?
   and a.attnum = ANY (ki.indkey)