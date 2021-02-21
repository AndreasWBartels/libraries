DECLARE 
  v_table_schema VARCHAR2 (30) := :in1;
  v_table_name VARCHAR2 (30) := :in2;
  v_column_name VARCHAR2 (30) := :in3;
  v_sequence_schema VARCHAR2 (30) := :in4;
  v_sequence_name VARCHAR2 (30) := :in5;
  v_count_table_value INTEGER;
  v_maximum_table_value INTEGER;
  v_current_seq_value INTEGER;
  v_increment INTEGER;
  BEGIN
    EXECUTE IMMEDIATE 'SELECT count(*) FROM ' || v_table_schema || '.' || v_table_name INTO v_count_table_value;
    IF v_count_table_value = 0 THEN
      RETURN;
    END IF;
    EXECUTE IMMEDIATE 'SELECT max(' || v_column_name || ') FROM ' || v_table_schema || '.' || v_table_name INTO v_maximum_table_value;
    EXECUTE IMMEDIATE 'SELECT ' || v_sequence_schema || '.' || v_sequence_name || '.NEXTVAL FROM dual' INTO v_current_seq_value;
    IF v_current_seq_value >=  v_maximum_table_value THEN
      RETURN;
    END IF;
    v_increment := v_maximum_table_value - v_current_seq_value;
    EXECUTE IMMEDIATE 'ALTER SEQUENCE ' || v_sequence_schema || '.' || v_sequence_name || ' INCREMENT BY ' || v_increment;
    EXECUTE IMMEDIATE 'SELECT ' || v_sequence_schema || '.' || v_sequence_name ||'.NEXTVAL FROM dual' INTO v_current_seq_value;
    EXECUTE IMMEDIATE 'ALTER SEQUENCE ' || v_sequence_schema || '.' || v_sequence_name || ' INCREMENT BY 1';
  END;