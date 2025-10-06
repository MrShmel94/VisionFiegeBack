CREATE SCHEMA IF NOT EXISTS attendance;

CREATE TABLE IF NOT EXISTS attendance.attendance_status(
    id SERIAL PRIMARY KEY,
    status_code VARCHAR(32) NOT NULL UNIQUE ,
    status_name VARCHAR(64) NOT NULL UNIQUE,
    display_front VARCHAR(64) NOT NULL UNIQUE ,
    description TEXT,
    color VARCHAR(32) NOT NULL
);

CREATE TABLE IF NOT EXISTS attendance.schedule_template(
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL,
    schedule JSONB NOT NULL,
    name_schedule_template TEXT NOT NULL ,
    user_id VARCHAR(512) NOT NULL,
    description TEXT,
    site_id INT NOT NULL REFERENCES vision.site(id),
    created_at TIMESTAMP DEFAULT now(),
    UNIQUE (name_schedule_template, site_id)
);

INSERT INTO attendance.attendance_status (status_name, status_code, display_front, color)
VALUES ('Leave on demand', 'UZ', 'UZ', '#34a853'),
       ('Vacation', 'U', 'U', '#34a853'),
       ('First day of sick leave', '1CH', '1CH', '#ffff00'),
       ('Sick leave', 'CH', 'CH', '#ffff00'),
       ('Other unplanned', 'INP', 'INP', '#a4c2f4'),
       ('Other planned', 'IP', 'IP', '#a4c2f4'),
       ('Unexcused absence', 'N', 'N','#ff0000'),
       ('Training', 'SZK', 'SZK', '#ff9900'),
       ('Health and Safety', 'BHP','BHP', '#ff9900'),
       ('Extension of sick leave', 'N?', '?','#ffffff'),
       ('Other', 'O','O','#ffffff'),
       ('Planed vacation', 'P','P', '#3c78d8'),
       ('Planed Work Day', 'Planed', '', '#ffffff'),
       ('In Work', 'Work', 'Work','#ffffff'),
       ('Not work', 'X', 'X', '#000000'),
       ('Day off', 'W','W', '#000000')
ON CONFLICT DO NOTHING;

CREATE TABLE IF NOT EXISTS attendance.attendance (
    id BIGSERIAL NOT NULL ,
    employee_id BIGINT REFERENCES vision.employee(id),
    date DATE NOT NULL,
    shift_id INT NOT NULL REFERENCES vision.shift_time_work(id),
    department_id INT NOT NULL REFERENCES vision.department(id),
    site_id INT NOT NULL REFERENCES vision.site(id),
    status_id INT NOT NULL REFERENCES attendance.attendance_status(id),
    hours_worked DOUBLE PRECISION DEFAULT 0,
    comment TEXT,
    user_id VARCHAR(512) NOT NULL,
    created_at TIMESTAMP DEFAULT now(),
    UNIQUE (employee_id, date, site_id)
) PARTITION BY RANGE (date);

CREATE INDEX idx_attendance_date_employee_id ON attendance.attendance(employee_id, date);
CREATE INDEX idx_attendance_date ON attendance.attendance(date);
CREATE INDEX idx_attendance_employee_id ON attendance.attendance(employee_id);
CREATE INDEX idx_attendance_site_id ON attendance.attendance(site_id);


CREATE OR REPLACE FUNCTION attendance.create_monthly_partition(target_date DATE)
    RETURNS VOID AS $$
DECLARE
    start_date DATE := date_trunc('month', target_date)::DATE;
    end_date DATE := (date_trunc('month', target_date) + interval '1 month')::DATE;
    partition_name TEXT := format('attendance.attendance_%s', to_char(start_date, 'YYYY_MM'));
BEGIN
    partition_name := format('attendance.attendance_%s', to_char(start_date, 'YYYY_MM'));

    EXECUTE format($sql$
        CREATE TABLE IF NOT EXISTS attendance.%I
        PARTITION OF attendance.attendance
        FOR VALUES FROM (%L) TO (%L)
    $sql$, partition_name, start_date, end_date);
END;
$$ LANGUAGE plpgsql
    SECURITY DEFINER;

CREATE OR REPLACE FUNCTION attendance.ensure_partition()
    RETURNS TRIGGER AS $$
BEGIN
    PERFORM 1
    FROM pg_class c
             JOIN pg_inherits i ON c.oid = i.inhrelid
    WHERE i.inhparent = 'attendance.attendance'::regclass
      AND c.relname = format('attendance.attendance_%s', to_char(NEW.date, 'YYYY_MM'));
    IF NOT FOUND THEN
        PERFORM attendance.create_monthly_partition(NEW.date);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_ensure_partition
    ON attendance.attendance;

CREATE TRIGGER trg_ensure_partition
    BEFORE INSERT ON attendance.attendance
    FOR EACH ROW
EXECUTE FUNCTION attendance.ensure_partition();

CREATE TABLE IF NOT EXISTS attendance.attendance_history (
    history_id    BIGSERIAL,
    audit_ts      TIMESTAMPTZ NOT NULL DEFAULT now(),
    operation     VARCHAR(52) NOT NULL,
    changed_by    VARCHAR(256),
    attendance_id BIGINT NOT NULL,
    employee_id   BIGINT,
    date          DATE,
    shift_id      INT,
    department_id INT,
    site_id       INT,
    status_id     INT,
    hours_worked  DOUBLE PRECISION,
    comment       TEXT,
    user_id       VARCHAR(512),
    created_at    TIMESTAMP,
    PRIMARY KEY (history_id, audit_ts)
) PARTITION BY RANGE (audit_ts);

CREATE OR REPLACE FUNCTION attendance.create_monthly_history_partition(target_ts TIMESTAMPTZ)
RETURNS VOID AS $$
DECLARE
    start_date TIMESTAMPTZ := date_trunc('month', target_ts);
    end_date   TIMESTAMPTZ := start_date + INTERVAL '1 month';
    partition_name TEXT := format('attendance_history_%s', to_char(start_date, 'YYYY_MM'));
BEGIN
    EXECUTE format($sql$
        CREATE TABLE IF NOT EXISTS attendance.%I
        PARTITION OF attendance.attendance_history
        FOR VALUES FROM (%L) TO (%L)
    $sql$, partition_name, start_date, end_date);
END;
$$ LANGUAGE plpgsql
   SECURITY DEFINER;

CREATE OR REPLACE FUNCTION attendance.audit_attendance()
  RETURNS TRIGGER AS $$
BEGIN

    PERFORM attendance.create_monthly_history_partition(now());

  IF TG_OP = 'INSERT' THEN
    INSERT INTO attendance.attendance_history(
      operation, changed_by,
      attendance_id, employee_id, date, shift_id, site_id, department_id,
      status_id, hours_worked, comment, user_id, created_at
    ) VALUES (
      'INSERT', NEW.user_id,
      NEW.id, NEW.employee_id, NEW.date, NEW.shift_id, NEW.site_id, NEW.department_id,
      NEW.status_id, NEW.hours_worked, NEW.comment, NEW.user_id, NEW.created_at
    );
    RETURN NEW;

  ELSIF TG_OP = 'UPDATE' THEN
    INSERT INTO attendance.attendance_history(
      operation, changed_by,
      attendance_id, employee_id, date, shift_id, site_id, department_id,
      status_id, hours_worked, comment, user_id, created_at
    ) VALUES (
      'UPDATE', NEW.user_id,
      NEW.id, NEW.employee_id, NEW.date, NEW.shift_id, NEW.site_id, NEW.department_id,
      NEW.status_id, NEW.hours_worked, NEW.comment, NEW.user_id, NEW.created_at
    );
    RETURN NEW;
  END IF;
END;
$$ LANGUAGE plpgsql
   SECURITY DEFINER;

DROP TRIGGER IF EXISTS trg_attendance_audit
  ON attendance.attendance;

CREATE TRIGGER trg_attendance_audit
  AFTER INSERT OR UPDATE
  ON attendance.attendance
  FOR EACH ROW
  EXECUTE FUNCTION attendance.audit_attendance();
