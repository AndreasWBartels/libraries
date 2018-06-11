DECLARE 
  seq_name  VARCHAR2 (255) := :in1;
  current_value  INTEGER;
  BEGIN
    EXECUTE IMMEDIATE 'ALTER SEQUENCE ' ||seq_name||' MINVALUE 0';
    EXECUTE IMMEDIATE 'SELECT ' ||seq_name ||'.NEXTVAL FROM dual' INTO current_value;
    IF current_value != 0 THEN
      IF current_value < 0 THEN
        EXECUTE IMMEDIATE 'ALTER SEQUENCE ' || seq_name || ' INCREMENT BY ' || current_value;
      ELSE
        EXECUTE IMMEDIATE 'ALTER SEQUENCE ' || seq_name || ' INCREMENT BY -' || current_value;
      END IF; 
      EXECUTE IMMEDIATE 'SELECT ' || seq_name ||'.NEXTVAL FROM dual' INTO current_value;
      EXECUTE IMMEDIATE 'ALTER SEQUENCE ' || seq_name || ' INCREMENT BY 1';
    END IF;
  END;