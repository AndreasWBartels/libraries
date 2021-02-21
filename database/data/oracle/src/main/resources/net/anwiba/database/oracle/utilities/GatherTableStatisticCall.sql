BEGIN
  DBMS_STATS.GATHER_TABLE_STATS (ownname => ?, tabname => ?, estimate_percent => 100);
END;