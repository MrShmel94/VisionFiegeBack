DO $$
DECLARE
i INT;
    supervisor_count INT := 50;
    site_rec RECORD;
    shift_rec RECORD;
    department_rec RECORD;
    position_rec RECORD;
    country_rec RECORD;
    agency_rec RECORD;
    team_rec RECORD;
    supervisor_rec RECORD;
    new_zalos_id SMALLINT;
    new_br_code VARCHAR(128);
BEGIN
FOR i IN 1..supervisor_count LOOP
SELECT * INTO site_rec FROM site ORDER BY RANDOM() LIMIT 1;
SELECT * INTO shift_rec FROM shift WHERE site_id = site_rec.id ORDER BY RANDOM() LIMIT 1;
SELECT * INTO department_rec FROM department WHERE site_id = site_rec.id ORDER BY RANDOM() LIMIT 1;
SELECT * INTO position_rec FROM position WHERE site_id = site_rec.id ORDER BY RANDOM() LIMIT 1;
SELECT * INTO country_rec FROM county WHERE site_id = site_rec.id ORDER BY RANDOM() LIMIT 1;
SELECT * INTO agency_rec FROM agency WHERE site_id = site_rec.id ORDER BY RANDOM() LIMIT 1;
SELECT * INTO team_rec FROM team WHERE site_id = site_rec.id ORDER BY RANDOM() LIMIT 1;

LOOP
    new_zalos_id := FLOOR(RANDOM() * 10000)::SMALLINT;
    EXIT WHEN NOT EXISTS (SELECT 1 FROM employee WHERE zalos_id = new_zalos_id);
END LOOP;

LOOP
    new_br_code := CONCAT('BR', FLOOR(RANDOM() * 10000)::TEXT);
    EXIT WHEN NOT EXISTS (SELECT 1 FROM employee WHERE br_code = new_br_code);
END LOOP;

INSERT INTO employee (
    expertis, zalos_id, br_code, first_name, last_name, sex, is_work,
    team_id, site_id, shift_id, country_id, department_id, position_id,
    supervisor_id, is_supervisor, agency_id, valid_from, valid_to
) VALUES (
             CONCAT('Expertis_', i),
             new_zalos_id,
             new_br_code,
             'John' || i,
             'Doe' || i,
             CASE WHEN RANDOM() > 0.5 THEN 'M' ELSE 'F' END,
             TRUE,
             team_rec.id,
             site_rec.id,
             shift_rec.id,
             country_rec.id,
             department_rec.id,
             position_rec.id,
             NULL,
             TRUE,
             agency_rec.id,
             NOW(),
             '9999-12-31 23:59:59'::TIMESTAMP
         );
END LOOP;

FOR i IN (supervisor_count + 1)..5000 LOOP
SELECT * INTO site_rec FROM site ORDER BY RANDOM() LIMIT 1;
SELECT * INTO shift_rec FROM shift WHERE site_id = site_rec.id ORDER BY RANDOM() LIMIT 1;
SELECT * INTO department_rec FROM department WHERE site_id = site_rec.id ORDER BY RANDOM() LIMIT 1;
SELECT * INTO position_rec FROM position WHERE site_id = site_rec.id ORDER BY RANDOM() LIMIT 1;
SELECT * INTO country_rec FROM county WHERE site_id = site_rec.id ORDER BY RANDOM() LIMIT 1;
SELECT * INTO agency_rec FROM agency WHERE site_id = site_rec.id ORDER BY RANDOM() LIMIT 1;
SELECT * INTO team_rec FROM team WHERE site_id = site_rec.id ORDER BY RANDOM() LIMIT 1;

SELECT * INTO supervisor_rec FROM employee WHERE is_supervisor = TRUE ORDER BY RANDOM() LIMIT 1;

LOOP
    new_zalos_id := FLOOR(RANDOM() * 10000)::SMALLINT;
    EXIT WHEN NOT EXISTS (SELECT 1 FROM employee WHERE zalos_id = new_zalos_id);
END LOOP;

LOOP
    new_br_code := CONCAT('BR', FLOOR(RANDOM() * 10000)::TEXT);
    EXIT WHEN NOT EXISTS (SELECT 1 FROM employee WHERE br_code = new_br_code);
END LOOP;

INSERT INTO employee (
    expertis, zalos_id, br_code, first_name, last_name, sex, is_work,
    team_id, site_id, shift_id, country_id, department_id, position_id,
    supervisor_id, is_supervisor, agency_id, valid_from, valid_to
) VALUES (
             CONCAT('Expertis_', i),
             new_zalos_id,
             new_br_code,
             'John' || i,
             'Doe' || i,
             CASE WHEN RANDOM() > 0.5 THEN 'M' ELSE 'F' END,
             TRUE,
             team_rec.id,
             site_rec.id,
             shift_rec.id,
             country_rec.id,
             department_rec.id,
             position_rec.id,
             supervisor_rec.id,
             FALSE,
             agency_rec.id,
             NOW(),
             '9999-12-31 23:59:59'::TIMESTAMP
         );
END LOOP;
END $$;