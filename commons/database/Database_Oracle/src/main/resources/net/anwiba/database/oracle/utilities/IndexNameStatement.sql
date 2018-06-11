select index_owner,
       index_name
  from all_ind_columns
 where table_owner = ?
   and table_name = ?
   and column_name = ?