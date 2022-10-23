 select v.column_name
      , v.type_name
      , COALESCE(c.character_maximum_length, numeric_precision, datetime_precision, -1) as column_size
      , COALESCE(c.numeric_scale, -1) as column_scale
      , v.is_nullable
      , v.auto_increment
  from (select s.nspname as schema_name
             , t.relname as table_name
             , a.attname as column_name
             , y.typname as type_name
             , not(a.attnotnull) as is_nullable
             , a.attnum as column_order
             , a.attidentity = 'd' or attidentity = 'a' as auto_increment
          from pg_namespace s,
               pg_class t,
               pg_attribute a,
               pg_type y
         where s.nspname = ?
           and a.attnum > 0
           and s.oid = t.relnamespace
           and t.relname = ?
           and t.oid = a.attrelid
           and a.atttypid = y.oid) v
   left JOIN information_schema.columns c ON c.table_schema = v.schema_name
   and c.table_name = v.table_name
   and c.column_name = v.column_name
order by v.column_order
