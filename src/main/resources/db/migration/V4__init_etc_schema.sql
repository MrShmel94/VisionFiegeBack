CREATE SCHEMA etc;

CREATE TABLE IF NOT EXISTS etc.type_documents(
    id SMALLSERIAL PRIMARY KEY ,
    name VARCHAR (128) NOT NULL UNIQUE ,
    description TEXT,
    user_id VARCHAR(512) NOT NULL ,
    date_create DATE NOT NULL DEFAULT now()
);

INSERT INTO etc.type_documents (name, description, user_id)
VALUES ('SOP', null, 'System'),
       ('BHP', null, 'System'),
       ('OPL', null, 'System');

CREATE TABLE etc.documents(
    id BIGSERIAL PRIMARY KEY ,
    name VARCHAR (128) NOT NULL UNIQUE ,
    description TEXT,
    type_document INT REFERENCES etc.type_documents (id),
    url TEXT,
    version INT NOT NULL ,
    date_last_upd DATE NOT NULL DEFAULT now(),
    date_start DATE NOT NULL DEFAULT now(),
    date_finish DATE NOT NULL DEFAULT now(),
    user_id VARCHAR (256) NOT NULL
);

CREATE INDEX idx_documents_name ON etc.documents(name);
CREATE INDEX idx_documents_type_document ON etc.documents(type_document);

CREATE TABLE etc.documents_history(
    history_id BIGSERIAL PRIMARY KEY ,
    original_id BIGINT NOT NULL,
    name VARCHAR (128) NOT NULL ,
    description TEXT,
    type_document INT REFERENCES etc.type_documents (id),
    url TEXT,
    version INT NOT NULL ,
    date_last_upd DATE NOT NULL DEFAULT now(),
    date_start DATE NOT NULL DEFAULT now(),
    date_finish DATE NOT NULL DEFAULT now(),
    user_id VARCHAR (256) NOT NULL,
    operation VARCHAR(10) NOT NULL,
    changed_at TIMESTAMP DEFAULT NOW() NOT NULL
);

CREATE INDEX idx_documents_history_original_id ON etc.documents_history(original_id);
CREATE INDEX idx_documents_history_original_id_changed_at ON etc.documents_history(original_id, changed_at DESC);
CREATE INDEX idx_documents_history_operation ON etc.documents_history(operation);
CREATE INDEX idx_documents_history_user_id ON etc.documents_history(user_id);


CREATE OR REPLACE FUNCTION etc.documents_audit_trigger()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS $$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        INSERT INTO etc.documents_history (
            original_id , name, description, type_document, url, version,
            date_last_upd, date_start, date_finish, user_id,
            operation, changed_at
        ) VALUES (
                     NEW.id, NEW.name, NEW.description, NEW.type_document, NEW.url, NEW.version,
                     NEW.date_last_upd, NEW.date_start, NEW.date_finish, NEW.user_id,
                     'INSERT',
                     NOW()
                 );
        RETURN NEW;

    ELSIF (TG_OP = 'UPDATE') THEN
        INSERT INTO etc.documents_history (
            original_id, name, description, type_document, url, version,
            date_last_upd, date_start, date_finish, user_id,
            operation, changed_at
        ) VALUES (
                     NEW.id, NEW.name, NEW.description, NEW.type_document, NEW.url, NEW.version,
                     NEW.date_last_upd, NEW.date_start, NEW.date_finish, NEW.user_id,
                     'UPDATE',
                     NOW()
                 );
        RETURN NEW;

    ELSIF (TG_OP = 'DELETE') THEN
        INSERT INTO etc.documents_history (
            original_id, name, description, type_document, url, version,
            date_last_upd, date_start, date_finish, user_id,
            operation, changed_at
        ) VALUES (
                     OLD.id, OLD.name, OLD.description, OLD.type_document, OLD.url, OLD.version,
                     OLD.date_last_upd, OLD.date_start, OLD.date_finish, COALESCE(current_setting('app.current_user_id', true), 'System'),
                     'DELETE',
                     NOW()
                 );
        RETURN OLD;
    END IF;

    RETURN NULL;
END;
$$;

CREATE TRIGGER documents_audit
    AFTER INSERT OR UPDATE OR DELETE
    ON etc.documents
    FOR EACH ROW
EXECUTE PROCEDURE etc.documents_audit_trigger();

CREATE TABLE etc.documents_department(
    id BIGSERIAL PRIMARY KEY,
    id_document INT REFERENCES etc.documents(id) ON DELETE CASCADE,
    id_department INT REFERENCES vision.department(id),
    user_id VARCHAR(512) NOT NULL,
    UNIQUE (id_document, id_department)
);

CREATE INDEX idx_documents_department_document_id ON etc.documents_department(id_document);
CREATE INDEX idx_documents_department_department_id ON etc.documents_department(id_department);

CREATE TABLE etc.documents_department_history(
    history_id BIGSERIAL PRIMARY KEY,
    original_id BIGINT NOT NULL,
    id_document INT NOT NULL,
    id_department INT NOT NULL,
    user_id VARCHAR(512) NOT NULL,
    operation VARCHAR(10) NOT NULL,
    changed_at TIMESTAMP DEFAULT now() NOT NULL
);

CREATE INDEX idx_documents_department_history_document_id ON etc.documents_department_history(id_document);
CREATE INDEX idx_documents_department_history_department_id ON etc.documents_department_history(id_department);
CREATE INDEX idx_documents_department_history_user_id ON etc.documents_department_history(user_id);
CREATE INDEX idx_documents_department_history_operation ON etc.documents_department_history(operation);

CREATE OR REPLACE FUNCTION etc.documents_department_audit_trigger()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        INSERT INTO etc.documents_department_history (
            original_id, id_document, id_department, user_id, operation, changed_at
        ) VALUES (
            NEW.id, NEW.id_document, NEW.id_department, NEW.user_id,

            'INSERT',
            NOW()
        );
        RETURN NEW;

    ELSIF (TG_OP = 'UPDATE') THEN
        INSERT INTO etc.documents_department_history (
            original_id, id_document, id_department, user_id, operation, changed_at
        ) VALUES (
            NEW.id, NEW.id_document, NEW.id_department, NEW.user_id,

            'UPDATE',
            NOW()
        );
        RETURN NEW;

    ELSIF (TG_OP = 'DELETE') THEN
        INSERT INTO etc.documents_department_history (
            original_id, id_document, id_department, operation, user_id, changed_at
        ) VALUES (
            OLD.id, OLD.id_document, OLD.id_department, 'DELETE',
            COALESCE(current_setting('app.current_user_id', true), 'System'),
            NOW()
        );
        RETURN OLD;
    END IF;

    RETURN NULL;
END;
$$;

CREATE TRIGGER documents_department_audit
    AFTER INSERT OR UPDATE OR DELETE
    ON etc.documents_department
    FOR EACH ROW
EXECUTE PROCEDURE etc.documents_department_audit_trigger();

CREATE TABLE etc.documents_position(
    id BIGSERIAL PRIMARY KEY,
    id_document INT REFERENCES etc.documents(id) ON DELETE CASCADE,
    id_position INT REFERENCES vision.position(id),
    user_id VARCHAR(512) NOT NULL ,
    UNIQUE (id_document, id_position)
);

CREATE INDEX idx_documents_position_document_id ON etc.documents_position(id_document);
CREATE INDEX idx_documents_position_position_id ON etc.documents_position(id_position);

CREATE TABLE etc.documents_position_history(
    history_id BIGSERIAL PRIMARY KEY,
    original_id BIGINT NOT NULL,
    id_document INT NOT NULL,
    id_position INT NOT NULL,
    user_id VARCHAR(512) NOT NULL,
    operation VARCHAR(10) NOT NULL,
    changed_at TIMESTAMP DEFAULT now() NOT NULL
);

CREATE INDEX idx_documents_position_history_document_id ON etc.documents_position_history(id_document);
CREATE INDEX idx_documents_position_history_position_id ON etc.documents_position_history(id_position);
CREATE INDEX idx_documents_position_history_user_id ON etc.documents_position_history(user_id);
CREATE INDEX idx_documents_position_history_operation ON etc.documents_position_history(operation);

CREATE OR REPLACE FUNCTION etc.documents_position_audit_trigger()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        INSERT INTO etc.documents_position_history (
            original_id, id_document, id_position, user_id, operation,  changed_at
        ) VALUES (
            NEW.id, NEW.id_document, NEW.id_position, NEW.user_id,
            'INSERT',
            NOW()
        );
        RETURN NEW;

        ELSIF (TG_OP = 'UPDATE') THEN
            INSERT INTO etc.documents_position_history (
                original_id, id_document, id_position, user_id, operation, changed_at
            ) VALUES (
            NEW.id, NEW.id_document, NEW.id_position, NEW.user_id,
            'UPDATE',
            NOW()
                     );
            RETURN NEW;

    ELSIF (TG_OP = 'DELETE') THEN
        INSERT INTO etc.documents_position_history (
            original_id, id_document, id_position, user_id, operation, changed_at
        ) VALUES (
            OLD.id, OLD.id_document, OLD.id_position,COALESCE(current_setting('app.current_user_id', true), 'System'),
            'DELETE',
            NOW()
        );
        RETURN OLD;
    END IF;

    RETURN NULL;
END;
$$;

CREATE TRIGGER documents_position_audit
    AFTER INSERT OR UPDATE OR DELETE
    ON etc.documents_position
    FOR EACH ROW
EXECUTE PROCEDURE etc.documents_position_audit_trigger();

CREATE TABLE etc.documents_position_assistance(
    id BIGSERIAL PRIMARY KEY,
    id_document INT REFERENCES etc.documents(id) ON DELETE CASCADE,
    id_position INT REFERENCES vision.position(id),
    user_id VARCHAR(512) NOT NULL,
    UNIQUE (id_document, id_position)
);

CREATE INDEX idx_documents_position_assistance_document_id ON etc.documents_position_assistance(id_document);
CREATE INDEX idx_documents_position_assistance_position_id ON etc.documents_position_assistance(id_position);

CREATE TABLE etc.documents_position_assistance_history(
    history_id BIGSERIAL PRIMARY KEY,
    original_id BIGINT NOT NULL,
    id_document INT NOT NULL,
    id_position INT NOT NULL,
    user_id VARCHAR(512) NOT NULL,
    operation VARCHAR(10) NOT NULL,
    changed_at TIMESTAMP DEFAULT now() NOT NULL
);

CREATE INDEX idx_documents_position_assistance_history_document_id ON etc.documents_position_assistance_history(id_document);
CREATE INDEX idx_documents_position_assistance_history_position_id ON etc.documents_position_assistance_history(id_position);
CREATE INDEX idx_documents_position_assistance_history_user_id ON etc.documents_position_assistance_history(user_id);
CREATE INDEX idx_documents_position_assistance_history_operation ON etc.documents_position_assistance_history(operation);

CREATE OR REPLACE FUNCTION etc.documents_position_assistance_audit_trigger()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        INSERT INTO etc.documents_position_assistance_history (
            original_id, id_document, id_position, user_id, operation, changed_at
        ) VALUES (
            NEW.id, NEW.id_document, NEW.id_position, NEW.user_id,
            'INSERT',
            NOW()
        );
        RETURN NEW;

    ELSIF (TG_OP = 'UPDATE') THEN
        INSERT INTO etc.documents_position_assistance_history (
            original_id, id_document, id_position, user_id, operation, changed_at
        ) VALUES (
            NEW.id, NEW.id_document, NEW.id_position,NEW.user_id,
            'UPDATE',
            NOW()
        );
        RETURN NEW;

    ELSIF (TG_OP = 'DELETE') THEN
        INSERT INTO etc.documents_position_assistance_history (
            original_id, id_document, id_position, user_id, operation,  changed_at
        ) VALUES (
            OLD.id, OLD.id_document, OLD.id_position,COALESCE(current_setting('app.current_user_id', true), 'System'),
            'DELETE',
            NOW()
        );
        RETURN OLD;
    END IF;

    RETURN NULL;
END;
$$;

CREATE TRIGGER documents_position_assistance_audit
    AFTER INSERT OR UPDATE OR DELETE
    ON etc.documents_position_assistance
    FOR EACH ROW
EXECUTE PROCEDURE etc.documents_position_assistance_audit_trigger();

CREATE TABLE etc.training_documents(
    id BIGSERIAL PRIMARY KEY ,
    employee_id INT REFERENCES vision.employee(id),
    document_id INT REFERENCES etc.documents(id) ON DELETE CASCADE ,
    date_training DATE NOT NULL DEFAULT now(),
    mentor_id INT REFERENCES vision.employee(id),
    version_training INT NOT NULL DEFAULT 0,
    user_training_id VARCHAR(512) NOT NULL
);

CREATE INDEX idx_training_doc_employee_id ON etc.training_documents(employee_id);
CREATE INDEX idx_training_doc_doc_id ON etc.training_documents(document_id);
CREATE INDEX idx_training_doc_user_id ON etc.training_documents(user_training_id);
CREATE INDEX idx_training_doc_user_dep ON etc.training_documents(employee_id, document_id);

CREATE TABLE etc.training_documents_history(
    history_id BIGSERIAL PRIMARY KEY ,
    original_id BIGINT NOT NULL,
    employee_id INT REFERENCES vision.employee(id),
    document_id INT REFERENCES etc.documents(id),
    date_training DATE NOT NULL DEFAULT now(),
    mentor_id INT REFERENCES vision.employee(id),
    version_training INT NOT NULL DEFAULT 0,
    user_training_id VARCHAR(512) NOT NULL ,
    operation VARCHAR(10) NOT NULL,
    changed_at TIMESTAMP DEFAULT NOW() NOT NULL
);

CREATE INDEX idx_training_his_doc_employee_id ON etc.training_documents_history(employee_id);
CREATE INDEX idx_training_his_doc_doc_id ON etc.training_documents_history(document_id);
CREATE INDEX idx_training_his_doc_user_id ON etc.training_documents_history(user_training_id);
CREATE INDEX idx_training_his_doc_user_dep ON etc.training_documents_history(employee_id, document_id);
CREATE INDEX idx_training_his_doc_operation ON etc.training_documents_history(operation);
CREATE INDEX idx_training_his_doc_changed_at ON etc.training_documents_history(changed_at DESC);

CREATE OR REPLACE FUNCTION etc.training_documents_audit_trigger()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS $$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        INSERT INTO etc.training_documents_history (
            original_id, employee_id, document_id, date_training, mentor_id, version_training, user_training_id,
            operation, changed_at
        ) VALUES (
                     NEW.id, NEW.employee_id, NEW.document_id, NEW.date_training, NEW.mentor_id, NEW.version_training, NEW.user_training_id,
                     'INSERT',
                     NOW()
                 );
        RETURN NEW;

    ELSIF  (TG_OP = 'UPDATE') THEN
        INSERT INTO etc.training_documents_history (
            original_id, employee_id, document_id, date_training, mentor_id, version_training, user_training_id,
            operation, changed_at
        ) VALUES (
                     NEW.id, NEW.employee_id, NEW.document_id, NEW.date_training, NEW.mentor_id, NEW.version_training, NEW.user_training_id,
                     'UPDATE',
                     NOW()
                 );
        RETURN NEW;

    ELSIF (TG_OP = 'DELETE') THEN
        INSERT INTO etc.training_documents_history (
            original_id, employee_id, document_id, date_training, mentor_id, version_training, user_training_id,
            operation, changed_at
        )VALUES (
                     OLD.id, OLD.employee_id, OLD.document_id, OLD.date_training, OLD.mentor_id, OLD.version_training, COALESCE(current_setting('app.current_user_id', true), 'System'),
                     'DELETE',
                     NOW()
                 );
        RETURN OLD;
    END IF;
END;
$$;

CREATE TRIGGER training_document_audit
    AFTER INSERT OR UPDATE OR DELETE
    ON etc.training_documents
    FOR EACH ROW
EXECUTE PROCEDURE etc.training_documents_audit_trigger();

CREATE TABLE IF NOT EXISTS etc.planed_training(
    id BIGSERIAL PRIMARY KEY ,
    date DATE NOT NULL,
    time_start TIME NOT NULL ,
    time_finish TIME NOT NULL ,
    document_id INT REFERENCES etc.documents(id) ON DELETE CASCADE ,
    positions JSONB NOT NULL,
    name_trainers JSONB NOT NULL ,
    max_count_employee INTEGER NOT NULL DEFAULT 1,
    place VARCHAR (256) NOT NULL,
    description TEXT NOT NULL ,
    is_auto_training BOOLEAN NOT NULL DEFAULT FALSE,
    user_id VARCHAR(512) NOT NULL
);

CREATE INDEX idx_planed_training_date ON etc.planed_training(date);
CREATE INDEX idx_planed_training_changed_by ON etc.planed_training(user_id);
CREATE INDEX idx_planed_training_document_id ON etc.planed_training(document_id);
CREATE INDEX idx_planed_training_date_time ON etc.planed_training(date, time_start);
CREATE INDEX idx_planed_training_document_date ON etc.planed_training(document_id, date);

CREATE TABLE etc.planed_training_history(
    history_id BIGSERIAL PRIMARY KEY,
    original_id BIGINT NOT NULL,
    date DATE NOT NULL,
    time_start TIME NOT NULL ,
    time_finish TIME NOT NULL ,
    document_id INT REFERENCES etc.documents(id),
    positions JSONB NOT NULL,
    name_trainers JSONB NOT NULL ,
    max_count_employee INTEGER NOT NULL DEFAULT 1,
    place VARCHAR (256) NOT NULL,
    description TEXT NOT NULL ,
    is_auto_training BOOLEAN NOT NULL DEFAULT FALSE,
    user_id VARCHAR(512) NOT NULL,
    operation VARCHAR(10) NOT NULL,
    changed_at TIMESTAMP DEFAULT NOW() NOT NULL
);

CREATE INDEX idx_planed_training_history_original_id ON etc.planed_training_history(original_id);
CREATE INDEX idx_planed_training_history_changed_at ON etc.planed_training_history(changed_at DESC);
CREATE INDEX idx_planed_training_history_changed_by ON etc.planed_training_history(user_id);
CREATE INDEX idx_planed_training_history_document_id ON etc.planed_training_history(document_id);
CREATE INDEX idx_planed_training_history_operation ON etc.planed_training_history(operation);

CREATE OR REPLACE FUNCTION etc.planed_training_audit_trigger()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS $$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        INSERT INTO etc.planed_training_history (
            original_id, date, time_start, time_finish, document_id,
            positions, name_trainers, max_count_employee, place, description, is_auto_training, user_id,
            operation, changed_at
        ) VALUES (
                     NEW.id, NEW.date, NEW.time_start, NEW.time_finish, NEW.document_id,
                     NEW.positions, NEW.name_trainers, NEW.max_count_employee, NEW.place, NEW.description, NEW.is_auto_training, NEW.user_id,
                     'INSERT',
                     NOW()
                 );
        RETURN NEW;

    ELSIF (TG_OP = 'UPDATE') THEN
        INSERT INTO etc.planed_training_history (
            original_id, date, time_start, time_finish, document_id,
            positions, name_trainers, max_count_employee, place, description, is_auto_training, user_id,
            operation, changed_at
        ) VALUES (
                     NEW.id, NEW.date, NEW.time_start, NEW.time_finish, NEW.document_id,
                     NEW.positions, NEW.name_trainers, NEW.max_count_employee, NEW.place, NEW.description, NEW.is_auto_training, NEW.user_id,
                     'UPDATE',
                     NOW()
                 );
        RETURN NEW;

    ELSIF (TG_OP = 'DELETE') THEN
        INSERT INTO etc.planed_training_history (
            original_id, date, time_start, time_finish, document_id,
            positions, name_trainers, max_count_employee, place, description, is_auto_training, user_id,
            operation, changed_at
        ) VALUES (
                     OLD.id, OLD.date, OLD.time_start, OLD.time_finish, OLD.document_id,
                     OLD.positions, OLD.name_trainers, OLD.max_count_employee, OLD.place, OLD.description, OLD.is_auto_training, COALESCE(current_setting('app.current_user_id', true), 'System'),
                     'DELETE',
                     NOW()
                 );
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$;

CREATE TRIGGER planed_training_audit
    AFTER INSERT OR UPDATE OR DELETE
    ON etc.planed_training
    FOR EACH ROW
EXECUTE PROCEDURE etc.planed_training_audit_trigger();

CREATE TABLE IF NOT EXISTS etc.planed_employee(
    id BIGSERIAL PRIMARY KEY ,
    employee_id BIGINT NOT NULL REFERENCES vision.employee(id),
    planed_training_id BIGINT NOT NULL REFERENCES etc.planed_training(id) ON DELETE CASCADE ,
    is_present BOOLEAN NOT NULL DEFAULT FALSE,
    date TIMESTAMP NOT NULL DEFAULT now(),
    user_id VARCHAR(512) NOT NULL,
    UNIQUE (employee_id, planed_training_id)
);

CREATE INDEX idx_planed_employee_employee_id ON etc.planed_employee(employee_id);
CREATE INDEX idx_planed_employee_training_id ON etc.planed_employee(planed_training_id);
CREATE INDEX idx_planed_employee_training_employee ON etc.planed_employee(planed_training_id, employee_id);

CREATE TABLE IF NOT EXISTS etc.planed_employee_history(
    history_id BIGSERIAL PRIMARY KEY ,
    original_id BIGINT NOT NULL,
    employee_id BIGINT NOT NULL REFERENCES vision.employee(id),
    planed_training_id BIGINT NOT NULL REFERENCES etc.planed_training(id),
    is_present BOOLEAN NOT NULL DEFAULT FALSE,
    date TIMESTAMP NOT NULL DEFAULT now(),
    user_id VARCHAR(512) NOT NULL,
    operation VARCHAR(10) NOT NULL,
    changed_at TIMESTAMP DEFAULT NOW() NOT NULL
);

CREATE INDEX idx_planed_employee_history_training_id ON etc.planed_employee_history(planed_training_id);
CREATE INDEX idx_planed_employee_history_employee_id ON etc.planed_employee_history(employee_id);
CREATE INDEX idx_planed_employee_history_user_id ON etc.planed_employee_history(user_id);
CREATE INDEX idx_planed_employee_history_operation ON etc.planed_employee_history(operation);
CREATE INDEX idx_planed_employee_history_changed_at ON etc.planed_employee_history(changed_at DESC);

CREATE OR REPLACE FUNCTION etc.planed_employee_audit_trigger()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS $$
BEGIN
    IF  (TG_OP = 'INSERT') THEN
        INSERT INTO etc.planed_employee_history (
            original_id, employee_id, planed_training_id, is_present, date, user_id,
            operation, changed_at
        ) VALUES (
                     NEW.id, NEW.employee_id, NEW.planed_training_id, NEW.is_present, NEW.date, NEW.user_id,
                     'INSERT',
                     NOW()
                 );
        RETURN NEW;

    ELSIF  (TG_OP = 'UPDATE') THEN
        INSERT INTO etc.planed_employee_history (
            original_id, employee_id, planed_training_id, is_present, date, user_id,
            operation, changed_at
        ) VALUES (
                     NEW.id, NEW.employee_id, NEW.planed_training_id, NEW.is_present, NEW.date, NEW.user_id,
                     'UPDATE',
                     NOW()
                 );
        RETURN NEW;

    ELSIF (TG_OP = 'DELETE') THEN
        INSERT INTO etc.planed_employee_history (
            original_id, employee_id, planed_training_id, is_present, date, user_id,
            operation, changed_at
        ) VALUES (
                     OLD.id, OLD.employee_id, OLD.planed_training_id, OLD.is_present, OLD.date, COALESCE(current_setting('app.current_user_id', true), 'System'),
                     'DELETE',
                     NOW()
                 );
        RETURN OLD;
    END IF;
END;
$$;

CREATE TRIGGER planed_employee_audit
    AFTER INSERT OR UPDATE OR DELETE
    ON etc.planed_employee
    FOR EACH ROW
EXECUTE PROCEDURE etc.planed_employee_audit_trigger();