CREATE SCHEMA performance_dg;

SET search_path TO performance_dg;

CREATE TABLE IF NOT EXISTS available_department(
    id SERIAL PRIMARY KEY,
    name VARCHAR (64) NOT NULL UNIQUE
);

INSERT INTO available_department (name)
VALUES ('Sort/Pack'),
       ('Pick/Stow'),
       ('Unloading'),
       ('Receive'),
       ('Shipping'),
       ('Quality'),
       ('Return'),
       ('Others');

CREATE TABLE IF NOT EXISTS spi_cluster (
    id SERIAL PRIMARY KEY,
    name VARCHAR (64) NOT NULL UNIQUE ,
    name_table VARCHAR (64) DEFAULT 'unknown'
);

INSERT INTO spi_cluster (name, name_table)
VALUES ('spi_I', 'Direct'),
       ('spi_II', 'NTT'),
       ('spi_III', 'Support'),
       ('timetracking', 'Timetracking'),
       ('ntt_unbezahlt', 'NTT - unbezahlt');

CREATE TABLE IF NOT EXISTS activity_name (
    id SERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE,
    spi_cluster INT NOT NULL REFERENCES spi_cluster
);

DO $$
DECLARE
    spi_I_id INT;
    spi_II_id INT;
    spi_III_id INT;
    timetracking_id INT;
    ntt_unbezahlt_id INT;
BEGIN
    SELECT id INTO spi_I_id FROM spi_cluster WHERE name = 'spi_I';
    SELECT id INTO spi_II_id FROM spi_cluster WHERE name = 'spi_II';
    SELECT id INTO spi_III_id FROM spi_cluster WHERE name = 'spi_III';
    SELECT id INTO timetracking_id FROM spi_cluster WHERE name = 'timetracking';
    SELECT id INTO ntt_unbezahlt_id FROM spi_cluster WHERE name = 'ntt_unbezahlt';

    INSERT INTO activity_name (name, spi_cluster)
    VALUES ('FASTLANE RECEIVE', spi_I_id),
           ('NCO PACK direct',spi_I_id),
           ('PACK_MULTI', spi_I_id),
           ('PACK_SINGLE', spi_I_id),
           ('PICK', spi_I_id),
           ('PREPARATION REPACKRECEIVE', spi_I_id),
           ('RECEIVE', spi_I_id),
           ('REPACKRECEIVE', spi_I_id),
           ('SORT', spi_I_id),
           ('STOW', spi_I_id),
           ('WMO_Outbound_Repacking', spi_I_id),
           ('DEFECT ITEM - CATEGORIZATION', spi_I_id),
           ('REREPLENISHMENT', spi_I_id),
           ('LINESORTPACK', spi_I_id),

           ('NTT Tätigkeitswechsel', spi_II_id),
           ('NTT_Teamwechsel', spi_II_id),
           ('NTT_Verteilzeit_Gehen', spi_II_id),
           ('NTT_Verteilzeit_Kommen', spi_II_id),
           ('TEAM REFORM SIGN-IN', spi_II_id),
           ('TEAM REFORM SIGN-OUT', spi_II_id),
           ('EMPLOYEE_INTERESTS', spi_II_id),
           ('NTT_keine_Tätigkeit_in_Schicht', spi_II_id),
           ('NTT_Teamwechsel_SignOut', spi_II_id),
           ('NTT_Teamwechsel_Transfer', spi_II_id),
           ('NTT in ENDACTIVITY', spi_II_id),
           ('FOERDERTECHNIK', spi_II_id),
           ('NTT in LINESORTPACK', spi_II_id),
           ('NTT in PACK_MULTI', spi_II_id),
           ('NTT in PACK_SINGLE', spi_II_id),
           ('NTT in SORT', spi_II_id),
           ('NTT in WMO_Outbound_Repacking', spi_II_id),
           ('NTT in NCO PACK direct', spi_II_id),
           ('NTT in PICK', spi_II_id),
           ('NTT in STOW', spi_II_id),
           ('NTT in RECEIVE', spi_II_id),
           ('NTT in FASTLANE RECEIVE', spi_II_id),
           ('NTT in REPACKING', spi_II_id),
           ('NTT in DEFECT ITEM - CATEGORIZATION', spi_II_id),
           ('NTT in NCOPACK', spi_II_id),
           ('NTT_Teamwechsel_SignIn', spi_II_id),
           ('NTT in REREPLENISHMENT', spi_II_id),
           ('NTT in CARTMANAGEMENT', spi_II_id),

           ('PROBLEMSOLVE', spi_III_id),
           ('MENTORING', spi_III_id),
           ('TEAMLEAD', spi_III_id),
           ('TRAINING', spi_III_id),
           ('FEEDBACK_TALKS', spi_III_id),
           ('SUPPORT', spi_III_id),
           ('RELOCATION direct', spi_III_id),
           ('VOLUMESCAN', spi_III_id),
           ('NTT in MENTORING', spi_III_id),
           ('EMPLOYER_INTERESTS', spi_III_id),
           ('NTT in FEEDBACK_TALKS', spi_III_id),
           ('SUPPORTPACK', spi_III_id),
           ('OPTIMUS_SUPPORT', spi_III_id),
           ('Internal_Orders_Shipping_Support', spi_III_id),
           ('WMO_Outbound_Loading', spi_III_id),
           ('NTT in WMO_Outbound_Loading', spi_III_id),
           ('NCOPACK', spi_III_id),
           ('SUPPORTRECEIVE', spi_III_id),
           ('GOODSRECEIVE', spi_III_id),
           ('WMO_Inbound', spi_III_id),
           ('YARDHOUSE', spi_III_id),
           ('REPACKING', spi_III_id),
           ('DEFECT ITEM - DEFECT SORT', spi_III_id),
           ('RETURN_REFURBISH', spi_III_id),
           ('NTT in DEFECT ITEM - DEFECT SORT', spi_III_id),
           ('Recom_Quality_Receive', spi_III_id),
           ('Recom_Support', spi_III_id),
           ('DOCKLOADER', spi_III_id),
           ('SUPPORTSHIPPING', spi_III_id),
           ('TECHNIKFACILITY', spi_III_id),
           ('CROSSDOCK', spi_III_id),
           ('HS_TRAINING', spi_III_id),
           ('EXTERNAL_PS', spi_III_id),
           ('AUDIT', spi_III_id),
           ('LPP', spi_III_id),
           ('CART RUNNER', spi_III_id),
           ('STOCKCLEANING', spi_III_id),
           ('STOCKTAKING direct', spi_III_id),
           ('NTT in STOCKTAKING direct', spi_III_id),
           ('SPACEMANAGEMENT', spi_III_id),
           ('BEAUTY_QUALITY', spi_III_id),
           ('NTT in HS_TRAINING', spi_III_id),
           ('INVENTURMANUELL', spi_III_id),
           ('SUPPORT_CALJAN', spi_III_id),
           ('NTT in RELOCATION direct', spi_III_id),
           ('NTT in CART RUNNER', spi_III_id),
           ('SUPPORTSORT', spi_III_id),
           ('NTT in SUPPORTRECEIVE', spi_III_id),
           ('MONTHLY_MEETING', spi_III_id),
           ('NTT in SPACEMANAGEMENT', spi_III_id),
           ('KWAPACKNCO', spi_III_id),
           ('CARTONS', spi_III_id),
           ('NTT in WMO_Inbound', spi_III_id),
           ('LIFTBOY', spi_III_id),
           ('NTT in OPTIMUS_SUPPORT', spi_III_id),
           ('NTT in SUPPORT', spi_III_id),
           ('NTT in FOERDERTECHNIK', spi_III_id),
           ('LOW_VOLUME', spi_III_id),
           ('NTT in DOCKLOADER', spi_III_id),
           ('SUPPORTRETURN', spi_III_id),
           ('QUALITYMANAGEMENT', spi_III_id),
           ('NTT in TRAINING', spi_III_id),
           ('NTT in PROBLEMSOLVE', spi_III_id),
           ('NTT in TECHNIKFACILITY', spi_III_id),
           ('WASTEMANAGEMENT', spi_III_id),
           ('INBOUND_ADMIN', spi_III_id),
           ('FORKLIFT', spi_III_id),
           ('NTT in VOLUMESCAN', spi_III_id),
           ('NTT in TEAMLEAD', spi_III_id),
           ('NTT in INVENTURMANUELL', spi_III_id),

           ('Timetracking - punch in', timetracking_id),
           ('Timetracking - punch out', timetracking_id),

           ('NTT_unbezahlt_Kommen', ntt_unbezahlt_id),
           ('NTT_unbezahlt_Gehen', ntt_unbezahlt_id);

END $$;

CREATE OR REPLACE FUNCTION set_default_department()
    RETURNS TRIGGER AS $$
BEGIN
    IF NEW.department_id IS NULL THEN
        NEW.department_id := (SELECT id FROM available_department WHERE name = 'Others' LIMIT 1);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TABLE IF NOT EXISTS final_cluster(
    id SERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE,
    department_id INT REFERENCES available_department
);

CREATE TRIGGER default_department_trigger
    BEFORE INSERT ON final_cluster
    FOR EACH ROW
EXECUTE FUNCTION set_default_department();

DO $$
    DECLARE
        pack_id INT;
        pick_id INT;
        receive_id INT;
        return_id INT;
        unloading_id INT;
        shipping_id INT;
        quality_id INT;
        others_id INT;
    BEGIN
        SELECT id INTO pack_id FROM available_department WHERE name = 'Sort/Pack';
        SELECT id INTO pick_id FROM available_department WHERE name = 'Pick/Stow';
        SELECT id INTO receive_id FROM available_department WHERE name = 'Receive';
        SELECT id INTO return_id FROM available_department WHERE name = 'Return';
        SELECT id INTO unloading_id FROM available_department WHERE name = 'Unloading';
        SELECT id INTO shipping_id FROM available_department WHERE name = 'Shipping';
        SELECT id INTO quality_id FROM available_department WHERE name = 'Quality';
        SELECT id INTO others_id FROM available_department WHERE name = 'Others';

        INSERT INTO final_cluster (name, department_id)
        VALUES ('Linesorter Pack', pack_id),
               ('Pack', pack_id),
               ('Standard Pack Multi', pack_id),
               ('Standard Pack Single', pack_id),
               ('Manual Sort', pack_id),
               ('Internal Orders Shipping', pack_id),
               ('NCO Shipping', pack_id),
               ('Pick', pick_id),
               ('Core Stow', pick_id),
               ('Shipping', shipping_id),
               ('Core Receive', receive_id),
               ('Fastlane Receive', receive_id),
               ('Goodsreceive', receive_id),
               ('Repackreceive', receive_id),
               ('Additional Effort',others_id),
               ('ActivityExclude',others_id),
               ('Refurbishment', return_id),
               ('Recommerce Receive', return_id),
               ('Core Retoure', return_id),
               ('Overhead',others_id);

    END $$;

CREATE TABLE IF NOT EXISTS check_header (
    id SERIAL PRIMARY KEY ,
    name VARCHAR (64) NOT NULL UNIQUE,
    table_name VARCHAR (128) NOT NULL UNIQUE
);

INSERT INTO check_header (name, table_name)
VALUES  ('date','Work Date Date'),
        ('expertis','Employee Personnel Number'),
        ('activityName','Activity Activity Name'),
        ('finalCluster', 'Final Cluster Final Cluster Name'),
        ('category', 'Activity Cluster Category Activity Cluster Category Name'),
        ('startActivity','Activity Start Time Activity Start Time'),
        ('endActivity', 'Activity End Time Activity End Time'),
        ('duration', 'Activity Duration (h)'),
        ('ql', '# Items Total'),
        ('qlBox', '# Received Items Boxed'),
        ('qlHanging', '# Received Items Hanging'),
        ('qlShoes', '# Received Items Shoes'),
        ('qlBoots','# Received Items Boots'),
        ('qlOther','# Received Items Other'),
        ('stowClarifications', '# Stow Clarifications'),
        ('pickNos1', '# Pick NOS1'),
        ('pickNos2', '# Pick NOS2'),
        ('qlReturn', '# Defect Item - Categorization'),
        ('sortReturn', '# Defect Item - Defect Sort'),
        ('qlWmo', '# Loaded WMOs to Load Carrier'),
        ('cartrunner', '# Cartrunner'),
        ('relocation', '# Relocation'),
        ('stocktaking', '# Stocktaking'),
        ('volumescan', '# Items Volumescan');

CREATE TABLE IF NOT EXISTS performance (
--     id BIGINT NOT NULL ,
    date DATE NOT NULL,
    expertis VARCHAR (64) NOT NULL,
    activity_name_id INT NOT NULL REFERENCES activity_name,
    final_cluster_id INT NOT NULL REFERENCES final_cluster,
    activity_cluster_id INT NOT NULL REFERENCES spi_cluster,
    start_activity TIMESTAMP NOT NULL ,
    end_activity TIMESTAMP NOT NULL,
    duration NUMERIC(10, 3) NOT NULL,
    ql SMALLINT DEFAULT 0,
    ql_box SMALLINT DEFAULT 0,
    ql_hanging SMALLINT DEFAULT 0,
    ql_shoes SMALLINT DEFAULT 0,
    ql_boots SMALLINT DEFAULT 0,
    ql_other SMALLINT DEFAULT 0,
    stow_clarifications SMALLINT DEFAULT 0,
    pick_nos1 SMALLINT DEFAULT 0,
    pick_nos2 SMALLINT DEFAULT 0
--     PRIMARY KEY (id, date)
) PARTITION BY RANGE (date);

CREATE INDEX idx_performance_date ON performance (date);
CREATE INDEX idx_performance_expertis ON performance (expertis);
CREATE INDEX idx_performance_activity_name ON performance (activity_name_id);
CREATE INDEX idx_performance_final_cluster ON performance (final_cluster_id);
CREATE INDEX idx_performance_start_activity ON performance (start_activity);
CREATE INDEX idx_performance_end_activity ON performance (end_activity);

-- CREATE SEQUENCE performance_seq START 1;

-- CREATE OR REPLACE FUNCTION create_partition_if_not_exists()
--     RETURNS TRIGGER AS $$
-- DECLARE
--     partition_name TEXT;
--     partition_start DATE;
--     partition_end DATE;
-- BEGIN
--     EXECUTE 'SET LOCAL plan_cache_mode = force_generic_plan';
--
--     partition_start := date_trunc('month', NEW.date)::DATE;
--     partition_end := (partition_start + INTERVAL '1 month')::DATE;
--     partition_name := 'performance_' || to_char(partition_start, 'YYYY_MM');
--
--     RAISE NOTICE 'Attempting to create partition for % from % to %', partition_name, partition_start, partition_end;
--
--     IF NOT EXISTS (
--         SELECT 1
--         FROM information_schema.tables
--         WHERE table_name = partition_name
--           AND table_schema = 'performance_dg'
--     ) THEN
--         RAISE NOTICE 'Creating partition % for range % to %', partition_name, partition_start, partition_end;
--
--         EXECUTE format('
--             CREATE TABLE performance_dg.%I PARTITION OF performance
--             FOR VALUES FROM (''%s'') TO (''%s'')',
--                        partition_name, partition_start, partition_end
--                 );
--         EXECUTE format('CREATE INDEX ON performance_dg.%I (date)', partition_name);
--         EXECUTE format('CREATE INDEX ON performance_dg.%I (expertis)', partition_name);
--         EXECUTE format('CREATE INDEX ON performance_dg.%I (activity_name_id)', partition_name);
--         EXECUTE format('CREATE INDEX ON performance_dg.%I (final_cluster_id)', partition_name);
--         EXECUTE format('CREATE INDEX ON performance_dg.%I (start_activity)', partition_name);
--         EXECUTE format('CREATE INDEX ON performance_dg.%I (end_activity)', partition_name);
--     ELSE
--         RAISE NOTICE 'Partition % already exists', partition_name;
--     END IF;
--
--     RETURN NEW;
-- EXCEPTION
--     WHEN OTHERS THEN
--         RAISE NOTICE 'Error occurred: %', SQLERRM;
--         RETURN NULL;
-- END;
-- $$ LANGUAGE plpgsql;
--
--
-- CREATE TRIGGER create_partition_trigger
--     BEFORE INSERT ON performance
--     FOR EACH ROW EXECUTE FUNCTION create_partition_if_not_exists();

CREATE TABLE IF NOT EXISTS check_name_file(
    id BIGSERIAL PRIMARY KEY ,
    date DATE NOT NULL,
    id_user_loaded VARCHAR(256) NOT NULL,
    name_file VARCHAR(256) NOT NULL UNIQUE,
    process VARCHAR(64) NOT NULL
);
