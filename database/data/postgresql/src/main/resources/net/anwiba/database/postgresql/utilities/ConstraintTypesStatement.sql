select a.attname,
       c.conname,
       c.contype,
       pg_get_constraintdef(c.oid)
  from pg_namespace s,
       pg_class t,
       pg_attribute a,
       pg_constraint c
 where s.nspname = ?
   and s.oid = t.relnamespace
   and t.relname = ?
   and t.oid = a.attrelid
   and t.oid = c.conrelid
   and a.attnum = ANY (c.conkey)