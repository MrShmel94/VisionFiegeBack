CREATE SCHEMA vision;

CREATE TABLE IF NOT EXISTS vision.site
(
    id SMALLSERIAL PRIMARY KEY,
    name VARCHAR(32) NOT NULL UNIQUE,
    place VARCHAR (64) NOT NULL,
    user_id VARCHAR (512) NOT NULL
);

CREATE TABLE IF NOT EXISTS vision.shift
(
    id SMALLSERIAL PRIMARY KEY,
    name VARCHAR(32) NOT NULL,
    site_id INTEGER NOT NULL REFERENCES vision.site,
    user_id VARCHAR (512) NOT NULL,
    UNIQUE(name, site_id)
);

CREATE TABLE IF NOT EXISTS vision.shift_time_work
(
    id SMALLSERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    shift_code VARCHAR(64) NOT NULL ,
    start_shift TIME NOT NULL,
    end_shift TIME NOT NULL,
    site_id INTEGER NOT NULL REFERENCES vision.site(id),
    user_id VARCHAR(512) NOT NULL
);

CREATE TABLE IF NOT EXISTS vision.team
(
    id SMALLSERIAL PRIMARY KEY,
    name VARCHAR(16) NOT NULL,
    site_id INTEGER NOT NULL REFERENCES vision.site,
    user_id VARCHAR(512) NOT NULL,
    UNIQUE(name, site_id)
);

CREATE TABLE IF NOT EXISTS vision.department
(
    id SMALLSERIAL PRIMARY KEY,
    name VARCHAR(32) NOT NULL ,
    site_id SMALLINT NOT NULL REFERENCES vision.site,
    user_id VARCHAR(512) NOT NULL,
    UNIQUE(name, site_id)
);


CREATE TABLE IF NOT EXISTS vision.position
(
    id SMALLSERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    site_id SMALLINT NOT NULL REFERENCES vision.site,
    user_id VARCHAR(512) NOT NULL,
    UNIQUE(name, site_id)
);

CREATE TABLE IF NOT EXISTS vision.county
(
    id SMALLSERIAL PRIMARY KEY,
    name VARCHAR(128),
    site_id SMALLINT NOT NULL REFERENCES vision.site,
    user_id VARCHAR(512) NOT NULL,
    UNIQUE(name, site_id)
);

CREATE TABLE IF NOT EXISTS vision.agency
(
    id SMALLSERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    site_id SMALLINT NOT NULL REFERENCES vision.site,
    user_id VARCHAR(512) NOT NULL,
    UNIQUE(name, site_id)
);

CREATE TABLE IF NOT EXISTS vision.user_registration
(
    id BIGSERIAL PRIMARY KEY,
    expertis VARCHAR(128) NOT NULL UNIQUE,
    email VARCHAR(256) NOT NULL UNIQUE,
    encrypted_password VARCHAR(256) NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE NOT NULL,
    email_verification_token VARCHAR(256),
    email_verification_status BOOLEAN DEFAULT FALSE NOT NULL,
    user_id VARCHAR(512) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS vision.favorite_search_groups (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES vision.user_registration(id),
    group_name VARCHAR(128) NOT NULL,
    expertis_list JSONB NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);


CREATE TABLE IF NOT EXISTS vision.employee
(
    id BIGSERIAL PRIMARY KEY,
    expertis VARCHAR(128) NOT NULL UNIQUE,
    br_code VARCHAR(128) UNIQUE,
    first_name VARCHAR(128) NOT NULL,
    last_name VARCHAR(128) NOT NULL,
    is_work BOOLEAN DEFAULT TRUE NOT NULL,
    sex VARCHAR(32) NOT NULL,
    team_id SMALLINT NOT NULL REFERENCES vision.team,
    site_id SMALLINT NOT NULL REFERENCES vision.site,
    shift_id SMALLINT NOT NULL REFERENCES vision.shift,
    country_id SMALLINT NOT NULL REFERENCES vision.county,
    department_id SMALLINT NOT NULL REFERENCES vision.department,
    position_id SMALLINT NOT NULL REFERENCES vision.position,
    is_supervisor BOOLEAN DEFAULT FALSE,
    is_can_has_account BOOLEAN DEFAULT FALSE,
    agency_id SMALLINT NOT NULL REFERENCES vision.agency,
    valid_from TIMESTAMP DEFAULT NOW() NOT NULL,
    valid_to TIMESTAMP DEFAULT (NOW() - INTERVAL '1 second')  NOT NULL,
    user_id VARCHAR(512) NOT NULL
);

CREATE INDEX idx_employee_expertis ON vision.employee (expertis);
CREATE INDEX idx_employee_site_dept_shift_team ON vision.employee (site_id, department_id, shift_id, team_id);
CREATE INDEX idx_employee_last_first ON vision.employee (last_name, first_name);
CREATE INDEX idx_employee_active ON vision.employee (id) WHERE is_work;
CREATE INDEX idx_lower_first_name ON vision.employee (LOWER(first_name));
CREATE INDEX idx_lower_last_name ON vision.employee (LOWER(last_name));
CREATE INDEX idx_employee_fullname ON vision.employee ((LOWER(first_name || ' ' || last_name)));
CREATE INDEX idx_employee_fullname_rev ON vision.employee ((LOWER(last_name || ' ' || first_name)));

CREATE TABLE IF NOT EXISTS vision.employee_supervisors (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL REFERENCES vision.employee(id) UNIQUE,
    supervisor_id BIGINT NOT NULL REFERENCES vision.employee(id),
    valid_from TIMESTAMP DEFAULT NOW() NOT NULL,
    valid_to TIMESTAMP DEFAULT '9999-12-31 23:59:59'::TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id VARCHAR(512) NOT NULL
);

CREATE INDEX idx_employee_supervisors_supervisor_id ON vision.employee_supervisors(supervisor_id);
CREATE INDEX idx_employee_supervisors_active ON vision.employee_supervisors(supervisor_id)
    WHERE valid_to = '9999-12-31 23:59:59';

CREATE TABLE IF NOT EXISTS vision.ai_employee
(
    id BIGSERIAL PRIMARY KEY,
    note VARCHAR(1028),
    date_start_contract DATE NOT NULL,
    date_finish_contract DATE NOT NULL,
    date_BHP_now DATE,
    date_BHP_future DATE,
    date_ADR_now DATE,
    date_ADR_future DATE,
    fte DOUBLE PRECISION DEFAULT 1,
    employee_id BIGINT NOT NULL REFERENCES vision.employee,
    user_id VARCHAR(512) NOT NULL
);


CREATE TABLE IF NOT EXISTS vision.phone_email_type_supervisor (
    id SERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE,
    user_id VARCHAR(512) NOT NULL
);

CREATE TABLE IF NOT EXISTS vision.email_supervisor
(
    id SERIAL PRIMARY KEY,
    type_id INT NOT NULL REFERENCES vision.phone_email_type_supervisor,
    email VARCHAR(256) NOT NULL UNIQUE,
    employee_id BIGINT REFERENCES vision.employee,
    user_id VARCHAR(512) NOT NULL
);

CREATE TABLE IF NOT EXISTS vision.phone_supervisor
(
    id SERIAL PRIMARY KEY,
    type_id INT NOT NULL REFERENCES vision.phone_email_type_supervisor,
    phone_number VARCHAR(128) NOT NULL UNIQUE,
    employee_id BIGINT REFERENCES vision.employee,
    user_id VARCHAR (512) NOT NULL
);

CREATE TABLE IF NOT EXISTS vision.role(
    id SERIAL PRIMARY KEY,
    name VARCHAR (64) NOT NULL UNIQUE,
    weight INTEGER NOT NULL DEFAULT 1,
    description TEXT,
    user_id VARCHAR(512) NOT NULL
);

CREATE TABLE vision.user_role (
    user_id BIGINT REFERENCES vision.employee(id),
    role_id INT REFERENCES vision.role(id),
    valid_from DATE DEFAULT NOW() NOT NULL,
    valid_to DATE DEFAULT '9999-12-31' NOT NULL,
    user_id_accept VARCHAR(512) NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE vision.refresh_tokens (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(512) NOT NULL,
    token VARCHAR(512) NOT NULL,
    expiration TIMESTAMP NOT NULL,
    revoked BOOLEAN DEFAULT FALSE,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE vision.login_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES vision.user_registration(id),
    login_time TIMESTAMP DEFAULT NOW(),
    logout_time TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent TEXT,
    success BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS vision.employee_history
(
    audit_id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    expertis VARCHAR(128) NOT NULL,
    br_code VARCHAR(128),
    first_name VARCHAR(128) NOT NULL,
    last_name VARCHAR(128) NOT NULL,
    is_work BOOLEAN DEFAULT TRUE NOT NULL,
    sex VARCHAR(32) NOT NULL,
    team_id SMALLINT NOT NULL,
    site_id SMALLINT NOT NULL,
    shift_id SMALLINT NOT NULL,
    country_id SMALLINT NOT NULL,
    department_id SMALLINT NOT NULL,
    position_id SMALLINT NOT NULL,
    supervisor_id SMALLINT,
    is_supervisor BOOLEAN DEFAULT FALSE,
    agency_id SMALLINT NOT NULL,
    valid_from TIMESTAMP NOT NULL,
    valid_to TIMESTAMP NOT NULL,
    user_id VARCHAR(512) NOT NULL ,
    operation VARCHAR(10) NOT NULL,
    changed_at TIMESTAMP DEFAULT NOW() NOT NULL
);

CREATE OR REPLACE FUNCTION vision.employee_audit_trigger()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS $$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        INSERT INTO vision.employee_history (
            employee_id, expertis, br_code, first_name, last_name,
            sex, is_work, team_id, site_id, shift_id, country_id, department_id, position_id,
            is_supervisor, agency_id, valid_from, valid_to, user_id,
            operation, changed_at
        ) VALUES (
                     NEW.id, NEW.expertis,  NEW.br_code, NEW.first_name, NEW.last_name,
                     NEW.sex, NEW.is_work, NEW.team_id, NEW.site_id, NEW.shift_id, NEW.country_id, NEW.department_id, NEW.position_id,
                     NEW.is_supervisor, NEW.agency_id, NEW.valid_from, NEW.valid_to, NEW.user_id,
                     'INSERT',
                     NOW()
                 );
        RETURN NEW;

    ELSIF (TG_OP = 'UPDATE' AND NEW IS DISTINCT FROM OLD) THEN
        INSERT INTO vision.employee_history (
            employee_id, expertis, br_code, first_name, last_name,
            sex, is_work, team_id, site_id, shift_id, country_id, department_id, position_id,
            is_supervisor, agency_id, valid_from, valid_to, user_id,
            operation, changed_at
        ) VALUES (
                     NEW.id, NEW.expertis,  NEW.br_code, NEW.first_name, NEW.last_name,
                     NEW.sex, NEW.is_work, NEW.team_id, NEW.site_id, NEW.shift_id, NEW.country_id, NEW.department_id, NEW.position_id,
                     NEW.is_supervisor, NEW.agency_id, NEW.valid_from, NEW.valid_to, NEW.user_id,
                     'UPDATE',
                     NOW()
                 );
        RETURN NEW;
    END IF;
END;
$$;

CREATE TRIGGER employee_audit
    AFTER INSERT OR UPDATE
    ON vision.employee
    FOR EACH ROW
EXECUTE PROCEDURE vision.employee_audit_trigger();

CREATE TABLE IF NOT EXISTS vision.user_role_history (
    audit_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    role_id INT,
    valid_from DATE,
    valid_to DATE,
    user_id_accept VARCHAR(512),
    operation VARCHAR(10) NOT NULL,
    changed_at TIMESTAMP DEFAULT NOW() NOT NULL
);

CREATE OR REPLACE FUNCTION vision.user_role_audit_trigger()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        INSERT INTO vision.user_role_history (
            user_id, role_id, valid_from, valid_to, user_id_accept,
            operation, changed_at
        ) VALUES (
            NEW.user_id, NEW.role_id, NEW.valid_from, NEW.valid_to, NEW.user_id_accept,
            'INSERT',
            NOW()
        );
        RETURN NEW;

    ELSIF (TG_OP = 'UPDATE' AND NEW IS DISTINCT FROM OLD) THEN
        INSERT INTO vision.user_role_history (
            user_id, role_id, valid_from, valid_to, user_id_accept,
            operation, changed_at
        ) VALUES (
            NEW.user_id, NEW.role_id, NEW.valid_from, NEW.valid_to, NEW.user_id_accept,
            'UPDATE',
            NOW()
        );
        RETURN NEW;

    ELSIF (TG_OP = 'DELETE') THEN
        INSERT INTO vision.user_role_history (
            user_id, role_id, valid_from, valid_to, user_id_accept,
            operation, changed_at
        ) VALUES (
            OLD.user_id, OLD.role_id, OLD.valid_from, OLD.valid_to,
            COALESCE(current_setting('app.current_user_id', true), 'System'),
            'DELETE',
            NOW()
        );
        RETURN OLD;
    END IF;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER user_role_audit
AFTER INSERT OR UPDATE OR DELETE
ON vision.user_role
FOR EACH ROW
EXECUTE FUNCTION vision.user_role_audit_trigger();

CREATE TABLE IF NOT EXISTS vision.ai_employee_history (
    audit_id BIGSERIAL PRIMARY KEY,
    id BIGINT,
    note VARCHAR(1028),
    date_start_contract DATE,
    date_finish_contract DATE,
    date_BHP_now DATE,
    date_BHP_future DATE,
    date_ADR_now DATE,
    date_ADR_future DATE,
    fte DOUBLE PRECISION,
    employee_id BIGINT,
    user_id VARCHAR(512),
    operation VARCHAR(10) NOT NULL,
    changed_at TIMESTAMP DEFAULT NOW() NOT NULL
);

CREATE OR REPLACE FUNCTION vision.ai_employee_audit_trigger()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        INSERT INTO vision.ai_employee_history (
            id, note, date_start_contract, date_finish_contract, date_BHP_now, date_BHP_future,
            date_ADR_now, date_ADR_future, fte, employee_id, user_id,
            operation, changed_at
        ) VALUES (
            NEW.id, NEW.note, NEW.date_start_contract, NEW.date_finish_contract, NEW.date_BHP_now, NEW.date_BHP_future,
            NEW.date_ADR_now, NEW.date_ADR_future, NEW.fte, NEW.employee_id, NEW.user_id,
            'INSERT',
            NOW()
        );
        RETURN NEW;

    ELSIF (TG_OP = 'UPDATE' AND NEW IS DISTINCT FROM OLD) THEN
        INSERT INTO vision.ai_employee_history (
            id, note, date_start_contract, date_finish_contract, date_BHP_now, date_BHP_future,
            date_ADR_now, date_ADR_future, fte, employee_id, user_id,
            operation, changed_at
        ) VALUES (
            NEW.id, NEW.note, NEW.date_start_contract, NEW.date_finish_contract, NEW.date_BHP_now, NEW.date_BHP_future,
            NEW.date_ADR_now, NEW.date_ADR_future, NEW.fte, NEW.employee_id, NEW.user_id,
            'UPDATE',
            NOW()
        );
        RETURN NEW;
    END IF;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER ai_employee_audit
AFTER INSERT OR UPDATE
ON vision.ai_employee
FOR EACH ROW
EXECUTE FUNCTION vision.ai_employee_audit_trigger();