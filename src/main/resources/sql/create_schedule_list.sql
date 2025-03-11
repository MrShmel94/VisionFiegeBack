CREATE SCHEMA IF NOT EXISTS attendance_gd;

SET search_path TO attendance_gd;

CREATE TABLE IF NOT EXISTS attendance_status(
    id SERIAL PRIMARY KEY,
    status_code VARCHAR(32) NOT NULL UNIQUE ,
    status_name VARCHAR(64) NOT NULL UNIQUE,
    description TEXT
);

INSERT INTO attendance_status (status_name, status_code)
VALUES ('Leave on demand', 'UZ'),
       ('Vacation', 'U'),
       ('First day of sick leave', '1CH'),
       ('Sick leave', 'CH'),
       ('Other unplanned', 'INP'),
       ('Other planned', 'IP'),
       ('Unexcused absence', 'N'),
       ('Training', 'SZK'),
       ('Health and Safety', 'BHP'),
       ('Extension of sick leave', 'N?'),
       ('Other', 'O'),
       ('Planed vacation', 'P'),
       ('Planed Work Day', ''),
       ('Day off', 'W');

CREATE TABLE IF NOT EXISTS attendance (
    id BIGSERIAL NOT NULL ,
    employee_id BIGINT REFERENCES vision.employee(id),
    date VARCHAR(32) NOT NULL,
    shift_id INT REFERENCES vision.shift_time_work(id),
    status_id INT REFERENCES attendance_status(id),
    hours_worked DOUBLE PRECISION DEFAULT 0,
    comment TEXT,
    user_id VARCHAR(256) NOT NULL,
    created_at TIMESTAMP DEFAULT now(),
    UNIQUE (employee_id, date)
) PARTITION BY RANGE (date);

CREATE INDEX idx_attendance_date_employee_id ON attendance(employee_id, date);
CREATE INDEX idx_attendance_date ON attendance(date);
CREATE INDEX idx_attendance_employee_id ON attendance(employee_id);

CREATE TABLE IF NOT EXISTS schedule_template(
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL,
    schedule JSONB NOT NULL,
    name_schedule_template VARCHAR(512) NOT NULL UNIQUE ,
    user_id VARCHAR(256) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT now()
);

CREATE OR REPLACE FUNCTION create_monthly_partition(target_date DATE) RETURNS VOID AS $$
DECLARE
    partition_name TEXT;
    start_date DATE := date_trunc('month', target_date)::DATE;
    end_date DATE := (date_trunc('month', target_date) + interval '1 month')::DATE;
BEGIN
    partition_name := format('attendance_%s', to_char(start_date, 'YYYY_MM'));

    EXECUTE format('
        CREATE TABLE IF NOT EXISTS %I PARTITION OF attendance
        FOR VALUES FROM (%L) TO (%L)
    ', partition_name, start_date, end_date);

    EXECUTE format('
        ALTER TABLE %I ADD CONSTRAINT %I_pk PRIMARY KEY (id)
    ', partition_name, partition_name);
END;
$$ LANGUAGE plpgsql;

SELECT create_monthly_partition('2025-03-01'::DATE);

-- CREATE TABLE IF NOT EXISTS attendance(
--     id BIGSERIAL PRIMARY KEY,
--     employee_id BIGINT REFERENCES vision.employee(id),
--     date DATE NOT NULL,
--     shift_id  INT REFERENCES vision.shift_time_work(id),
--     status_id INT REFERENCES attendance_status(id),
--     house_worked DOUBLE PRECISION NOT NULL,
--     comment TEXT,
--     user_id VARCHAR(256) NOT NULL,
--     date_time TIMESTAMP DEFAULT now()
-- );

