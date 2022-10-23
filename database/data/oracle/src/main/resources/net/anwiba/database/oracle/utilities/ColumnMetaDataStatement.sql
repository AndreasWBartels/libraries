   SELECT t.column_name AS column_name
        , t.data_type AS TYPE_NAME
        , decode(t.data_precision,   NULL,   decode(t.data_type,   'NUMBER',   -1, t.data_length),   t.data_precision) AS column_size
        , decode(t.data_scale,   NULL,   -1,   t.data_scale) AS column_scale
        , decode(t.nullable,   'N',   'FALSE',   'TRUE') AS is_nullable
        , decode(i.GENERATION_TYPE,   NULL,   'FALSE',   'TRUE') AS is_autoincrement
     FROM SYS.ALL_TAB_COLUMNS t
LEFT JOIN SYS.ALL_TAB_IDENTITY_COLS i ON t.owner = i.owner and t.TABLE_NAME = i.TABLE_NAME and t.column_name = i.column_name
    WHERE t.owner = ?
      AND t.TABLE_NAME = ?
 ORDER BY t.owner,
          t.TABLE_NAME,
          t.column_id