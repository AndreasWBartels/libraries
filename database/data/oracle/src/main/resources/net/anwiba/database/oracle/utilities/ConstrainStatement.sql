SELECT o.owner,
       o.table_name,
       replace(o.column_name, '"') AS column_name,
       c.constraint_type
  FROM all_constraints c,
       all_cons_columns o
 WHERE o.owner = ?
   AND o.table_name = ?
   AND o.owner = c.owner
   AND o.table_name = c.table_name
   AND o.constraint_name = c.constraint_name