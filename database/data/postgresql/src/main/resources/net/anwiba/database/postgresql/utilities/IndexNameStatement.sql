select s.nspname as schema,
       i.relname as index
  from pg_index ki,
       pg_namespace s,
       pg_class t,
       pg_class i,
       pg_attribute a
 where ki.indrelid = t.oid
   and ki.indexrelid = i.oid
   and s.oid = t.relnamespace
   and t.oid = a.attrelid
   and a.attnum = ANY (ki.indkey)
   and s.nspname = ?
   and t.relname = ?
   and a.attname = ?