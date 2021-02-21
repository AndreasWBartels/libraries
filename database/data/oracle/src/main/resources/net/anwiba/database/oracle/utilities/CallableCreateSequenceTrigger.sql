CREATE OR REPLACE TRIGGER {0} BEFORE INSERT ON {1} FOR EACH ROW
  DECLARE
    v_nextval INTEGER;
  BEGIN
    WHILE (:NEW.{2} IS NULL) LOOP
      SELECT {3}.nextval INTO v_nextval FROM dual;
     :NEW.{2} := v_nextval;
    END LOOP;
  END;
