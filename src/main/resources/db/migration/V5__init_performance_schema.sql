CREATE SCHEMA performance_gd;

CREATE TABLE IF NOT EXISTS performance_gd.spi_cluster (
    id SERIAL PRIMARY KEY,
    name VARCHAR (64) NOT NULL UNIQUE ,
    name_table VARCHAR (64) DEFAULT 'unknown'
);

INSERT INTO performance_gd.spi_cluster (name, name_table)
VALUES ('spi_I', 'Direct'),
       ('spi_II', 'NTT'),
       ('spi_III', 'Support'),
       ('timetracking', 'Timetracking'),
       ('ntt_unbezahlt', 'NTT - unbezahlt');

CREATE TABLE IF NOT EXISTS performance_gd.activity_name (
    id SERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE,
    spi_cluster INT NOT NULL REFERENCES performance_gd.spi_cluster
);

DO $$
DECLARE
    spi_I_id INT;
    spi_II_id INT;
    spi_III_id INT;
    timetracking_id INT;
    ntt_unbezahlt_id INT;
BEGIN
    SELECT id INTO spi_I_id FROM performance_gd.spi_cluster WHERE name = 'spi_I';
    SELECT id INTO spi_II_id FROM performance_gd.spi_cluster WHERE name = 'spi_II';
    SELECT id INTO spi_III_id FROM performance_gd.spi_cluster WHERE name = 'spi_III';
    SELECT id INTO timetracking_id FROM performance_gd.spi_cluster WHERE name = 'timetracking';
    SELECT id INTO ntt_unbezahlt_id FROM performance_gd.spi_cluster WHERE name = 'ntt_unbezahlt';

    INSERT INTO performance_gd.activity_name (name, spi_cluster)
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

CREATE TABLE IF NOT EXISTS performance_gd.final_cluster(
    id SERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS performance_gd.check_header (
    id SERIAL PRIMARY KEY ,
    name VARCHAR (64) NOT NULL UNIQUE,
    table_name VARCHAR (128) NOT NULL UNIQUE
);

INSERT INTO performance_gd.check_header (name, table_name)
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
        ('idleTime', 'Idle Time Duration in Activity (h)'),
        ('idleCount', '# Idle Times in Activity'),
        ('volumescan', '# Items Volumescan');

CREATE TABLE IF NOT EXISTS performance_gd.additional_effort(
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL,
    expertis VARCHAR(64) NOT NULL ,
    activity_name_id INT NOT NULL REFERENCES performance_gd.activity_name(id),
    duration DOUBLE PRECISION NOT NULL,
    shift_id INT NOT NULL REFERENCES vision.shift_time_work(id)
);

CREATE INDEX idx_ae_date ON performance_gd.additional_effort (date);
CREATE INDEX idx_ae_expertis ON performance_gd.additional_effort (expertis);
CREATE INDEX idx_ae_activity_name ON performance_gd.additional_effort (activity_name_id);
CREATE INDEX idx_ae_shift ON performance_gd.additional_effort (shift_id);


CREATE TABLE IF NOT EXISTS performance_gd.performance (
    date DATE NOT NULL,
    expertis VARCHAR (64) NOT NULL,
    activity_name_id INT NOT NULL REFERENCES performance_gd.activity_name,
    final_cluster_id INT NOT NULL REFERENCES performance_gd.final_cluster,
    activity_cluster_id INT NOT NULL REFERENCES performance_gd.spi_cluster,
    start_activity TIMESTAMP NOT NULL ,
    end_activity TIMESTAMP NOT NULL,
    duration DOUBLE PRECISION NOT NULL,
    duration_idle DOUBLE PRECISION NOT NULL,
    idle_count INT DEFAULT 0,
    ql INT DEFAULT 0,
    ql_box INT DEFAULT 0,
    ql_hanging INT DEFAULT 0,
    ql_shoes INT DEFAULT 0,
    ql_boots INT DEFAULT 0,
    ql_other INT DEFAULT 0,
    stow_clarifications INT DEFAULT 0,
    pick_nos1 INT DEFAULT 0,
    pick_nos2 INT DEFAULT 0
) PARTITION BY RANGE (date);

CREATE INDEX idx_performance_date ON performance_gd.performance (date);
CREATE INDEX idx_performance_expertis ON performance_gd.performance (expertis);
CREATE INDEX idx_performance_date_expertis ON performance_gd.performance (date, expertis);
CREATE INDEX idx_performance_activity_name ON performance_gd.performance (activity_name_id);
CREATE INDEX idx_performance_final_cluster ON performance_gd.performance (final_cluster_id);
CREATE INDEX idx_performance_start_activity ON performance_gd.performance (start_activity);
CREATE INDEX idx_performance_end_activity ON performance_gd.performance (end_activity);

CREATE TABLE IF NOT EXISTS performance_gd.check_name_file(
    id BIGSERIAL PRIMARY KEY ,
    date DATE NOT NULL,
    id_user_loaded VARCHAR(512) NOT NULL,
    name_file VARCHAR(256) NOT NULL UNIQUE,
    status VARCHAR(64) NOT NULL
);

CREATE SEQUENCE performance_gd.clear_performance_employee_id_seq
    INCREMENT 200
    START WITH 1;

CREATE TABLE IF NOT EXISTS performance_gd.clear_performance_employee(
    id BIGINT PRIMARY KEY DEFAULT nextval('performance_gd.clear_performance_employee_id_seq'),
    date DATE NOT NULL ,
    expertis VARCHAR(64) NOT NULL ,
    --PACK PROCESSING
    ql_opt INT DEFAULT 0,
    time_opt DOUBLE PRECISION DEFAULT 0,
    support_opt DOUBLE PRECISION DEFAULT 0,
    ntt_opt DOUBLE PRECISION DEFAULT 0,
    ql_multi INT DEFAULT 0,
    time_multi DOUBLE PRECISION DEFAULT 0,
    ntt_multi DOUBLE PRECISION DEFAULT 0,
    ql_single INT DEFAULT 0,
    time_single DOUBLE PRECISION DEFAULT 0,
    ntt_single DOUBLE PRECISION DEFAULT 0,
    support_pack DOUBLE PRECISION DEFAULT 0,
    ntt_pack DOUBLE PRECISION DEFAULT 0,
    ql_sort INT DEFAULT 0,
    time_sort DOUBLE PRECISION DEFAULT 0,
    support_sort DOUBLE PRECISION DEFAULT 0,
    ntt_sort DOUBLE PRECISION DEFAULT 0,
    ql_wmo INT DEFAULT 0,
    time_wmo DOUBLE PRECISION DEFAULT 0,
    support_wmo DOUBLE PRECISION DEFAULT 0,
    ntt_wmo DOUBLE PRECISION DEFAULT 0,
    ql_nco INT DEFAULT 0,
    time_nco DOUBLE PRECISION DEFAULT 0,
    support_nco DOUBLE PRECISION DEFAULT 0,
    ntt_nco DOUBLE PRECISION DEFAULT 0,
    --PICK PROCESSING
    ql_stow INT DEFAULT 0,
    time_stow DOUBLE PRECISION DEFAULT 0,
    support_stow DOUBLE PRECISION DEFAULT 0,
    ntt_stow DOUBLE PRECISION DEFAULT 0,
    ql_pick INT DEFAULT 0,
    time_pick DOUBLE PRECISION DEFAULT 0,
    support_pick DOUBLE PRECISION DEFAULT 0,
    ntt_pick DOUBLE PRECISION DEFAULT 0,
    --SHIPPING PROCESSING
    support_shipping DOUBLE PRECISION DEFAULT 0,
    ntt_shipping DOUBLE PRECISION DEFAULT 0,
    --RETURN PROCESSING
    ql_return INT DEFAULT 0,
    time_return DOUBLE PRECISION DEFAULT 0,
    support_return DOUBLE PRECISION DEFAULT 0,
    ntt_return DOUBLE PRECISION DEFAULT 0,
    --RECEIVE PROCESSING
    ql_fast INT DEFAULT 0,
    time_fast DOUBLE PRECISION DEFAULT 0,
    ntt_fast DOUBLE PRECISION DEFAULT 0,
    ql_repack INT DEFAULT 0,
    time_repack DOUBLE PRECISION DEFAULT 0,
    support_repack DOUBLE PRECISION DEFAULT 0,
    ql_repl INT DEFAULT 0,
    time_repl DOUBLE PRECISION DEFAULT 0,
    ql_core INT DEFAULT 0,
    ql_core_boxed INT DEFAULT 0,
    ql_core_hanging INT DEFAULT 0,
    ql_core_shoes INT DEFAULT 0,
    ql_core_boots INT DEFAULT 0,
    ql_core_other INT DEFAULT 0,
    time_core DOUBLE PRECISION DEFAULT 0,
    support_core DOUBLE PRECISION DEFAULT 0,
    ntt_core DOUBLE PRECISION DEFAULT 0,
    --UNLOADING PROCESSING
    support_goods DOUBLE PRECISION DEFAULT 0,
    ntt_goods DOUBLE PRECISION DEFAULT 0,
    --ADDITIONAL PROCESSING
    duration_idle DOUBLE PRECISION NOT NULL,
    idle_count INT DEFAULT 0,
    ql_relocation INT DEFAULT 0,
    time_relocation DOUBLE PRECISION DEFAULT 0,
    ql_volume_scan INT DEFAULT 0,
    time_volume_scan DOUBLE PRECISION DEFAULT 0,
    ql_return_sort INT DEFAULT 0,
    time_return_sort DOUBLE PRECISION DEFAULT 0,
    ql_stocktaking INT DEFAULT 0,
    time_stocktaking DOUBLE PRECISION DEFAULT 0,
    pick_nos_1 INT DEFAULT 0,
    pick_nos_2 INT DEFAULT 0,
    stow_clarifications INT DEFAULT 0,

    shift_id INT REFERENCES vision.shift_time_work(id)
);

CREATE INDEX idx_performance_clear_date ON performance_gd.clear_performance_employee (date);
CREATE INDEX idx_performance_clear_expertis ON performance_gd.clear_performance_employee (expertis);
CREATE INDEX idx_performance_clear_date_shift ON performance_gd.clear_performance_employee (date, shift_id);
CREATE INDEX idx_performance_clear_expertis_date ON performance_gd.clear_performance_employee (expertis, date);
CREATE INDEX idx_performance_clear_shift_id ON performance_gd.clear_performance_employee (shift_id);

CREATE SEQUENCE performance_gd.clear_performance_employee_without_ntt_id_seq
    INCREMENT 200
    START WITH 1;

CREATE TABLE IF NOT EXISTS performance_gd.clear_performance_employee_without_ntt_tatig(
    id BIGINT PRIMARY KEY DEFAULT nextval('performance_gd.clear_performance_employee_without_ntt_id_seq'),
    date DATE NOT NULL ,
    expertis VARCHAR(64) NOT NULL ,
    --PACK PROCESSING
    ql_opt INT DEFAULT 0,
    time_opt DOUBLE PRECISION DEFAULT 0,
    support_opt DOUBLE PRECISION DEFAULT 0,
    ntt_opt DOUBLE PRECISION DEFAULT 0,
    ql_multi INT DEFAULT 0,
    time_multi DOUBLE PRECISION DEFAULT 0,
    ntt_multi DOUBLE PRECISION DEFAULT 0,
    ql_single INT DEFAULT 0,
    time_single DOUBLE PRECISION DEFAULT 0,
    ntt_single DOUBLE PRECISION DEFAULT 0,
    support_pack DOUBLE PRECISION DEFAULT 0,
    ntt_pack DOUBLE PRECISION DEFAULT 0,
    ql_sort INT DEFAULT 0,
    time_sort DOUBLE PRECISION DEFAULT 0,
    support_sort DOUBLE PRECISION DEFAULT 0,
    ntt_sort DOUBLE PRECISION DEFAULT 0,
    ql_wmo INT DEFAULT 0,
    time_wmo DOUBLE PRECISION DEFAULT 0,
    support_wmo DOUBLE PRECISION DEFAULT 0,
    ntt_wmo DOUBLE PRECISION DEFAULT 0,
    ql_nco INT DEFAULT 0,
    time_nco DOUBLE PRECISION DEFAULT 0,
    support_nco DOUBLE PRECISION DEFAULT 0,
    ntt_nco DOUBLE PRECISION DEFAULT 0,
    --PICK PROCESSING
    ql_stow INT DEFAULT 0,
    time_stow DOUBLE PRECISION DEFAULT 0,
    support_stow DOUBLE PRECISION DEFAULT 0,
    ntt_stow DOUBLE PRECISION DEFAULT 0,
    ql_pick INT DEFAULT 0,
    time_pick DOUBLE PRECISION DEFAULT 0,
    support_pick DOUBLE PRECISION DEFAULT 0,
    ntt_pick DOUBLE PRECISION DEFAULT 0,
    --SHIPPING PROCESSING
    support_shipping DOUBLE PRECISION DEFAULT 0,
    ntt_shipping DOUBLE PRECISION DEFAULT 0,
    --RETURN PROCESSING
    ql_return INT DEFAULT 0,
    time_return DOUBLE PRECISION DEFAULT 0,
    support_return DOUBLE PRECISION DEFAULT 0,
    ntt_return DOUBLE PRECISION DEFAULT 0,
    --RECEIVE PROCESSING
    ql_fast INT DEFAULT 0,
    time_fast DOUBLE PRECISION DEFAULT 0,
    ntt_fast DOUBLE PRECISION DEFAULT 0,
    ql_repack INT DEFAULT 0,
    time_repack DOUBLE PRECISION DEFAULT 0,
    support_repack DOUBLE PRECISION DEFAULT 0,
    ql_repl INT DEFAULT 0,
    time_repl DOUBLE PRECISION DEFAULT 0,
    ql_core INT DEFAULT 0,
    ql_core_boxed INT DEFAULT 0,
    ql_core_hanging INT DEFAULT 0,
    ql_core_shoes INT DEFAULT 0,
    ql_core_boots INT DEFAULT 0,
    ql_core_other INT DEFAULT 0,
    time_core DOUBLE PRECISION DEFAULT 0,
    support_core DOUBLE PRECISION DEFAULT 0,
    ntt_core DOUBLE PRECISION DEFAULT 0,
    --UNLOADING PROCESSING
    support_goods DOUBLE PRECISION DEFAULT 0,
    ntt_goods DOUBLE PRECISION DEFAULT 0,
    --ADDITIONAL PROCESSING
    duration_idle DOUBLE PRECISION NOT NULL,
    idle_count INT DEFAULT 0,
    ql_relocation INT DEFAULT 0,
    time_relocation DOUBLE PRECISION DEFAULT 0,
    ql_volume_scan INT DEFAULT 0,
    time_volume_scan DOUBLE PRECISION DEFAULT 0,
    ql_return_sort INT DEFAULT 0,
    time_return_sort DOUBLE PRECISION DEFAULT 0,
    ql_stocktaking INT DEFAULT 0,
    time_stocktaking DOUBLE PRECISION DEFAULT 0,
    pick_nos_1 INT DEFAULT 0,
    pick_nos_2 INT DEFAULT 0,
    stow_clarifications INT DEFAULT 0,

    shift_id SMALLINT REFERENCES vision.shift_time_work(id)
);

CREATE INDEX idx_performance_clear_date_without_ntt ON performance_gd.clear_performance_employee_without_ntt_tatig (date);
CREATE INDEX idx_performance_clear_expertis_without_ntt ON performance_gd.clear_performance_employee_without_ntt_tatig (expertis);
CREATE INDEX idx_performance_clear_date_shift_without_ntt ON performance_gd.clear_performance_employee_without_ntt_tatig (date, shift_id);
CREATE INDEX idx_performance_clear_expertis_date_without_ntt ON performance_gd.clear_performance_employee_without_ntt_tatig (expertis, date);
CREATE INDEX idx_performance_clear_shift_id_without_ntt ON performance_gd.clear_performance_employee_without_ntt_tatig (shift_id);

CREATE TABLE IF NOT EXISTS performance_gd.spi_gd(
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL,
    shift_id INT REFERENCES vision.shift_time_work(id),
    --CORE RECEIVE
    spi_I_core DOUBLE PRECISION DEFAULT 0,
    spi_II_core DOUBLE PRECISION DEFAULT 0,
    spi_III_core DOUBLE PRECISION DEFAULT 0,
    core_total_ql INT DEFAULT 0,
    core_shoes_ql INT DEFAULT 0,
    core_total_time DOUBLE PRECISION DEFAULT 0,
    --REPACK RECEIVE
    spi_I_repack DOUBLE PRECISION DEFAULT 0,
    spi_III_repack DOUBLE PRECISION DEFAULT 0,
    repack_total_ql INT DEFAULT 0,
    repack_total_time DOUBLE PRECISION DEFAULT 0,
    --FASTLINE RECEIVE
    spi_I_fast DOUBLE PRECISION DEFAULT 0,
    spi_II_fast DOUBLE PRECISION DEFAULT 0,
    spi_III_fast DOUBLE PRECISION DEFAULT 0,
    fast_total_ql INT DEFAULT 0,
    fast_total_time DOUBLE PRECISION DEFAULT 0,
    --GOODS
    spi_II_goods DOUBLE PRECISION DEFAULT 0,
    spi_III_goods DOUBLE PRECISION DEFAULT 0,
    goods_total_ql INT DEFAULT 0,
    goods_total_time DOUBLE PRECISION DEFAULT 0,
    --SHIPPING
    spi_II_shipping DOUBLE PRECISION DEFAULT 0,
    spi_III_shipping DOUBLE PRECISION DEFAULT 0,
    shipping_total_ql INT DEFAULT 0,
    shipping_total_time DOUBLE PRECISION DEFAULT 0,
    --STOW
    spi_I_stow DOUBLE PRECISION DEFAULT 0,
    spi_II_stow DOUBLE PRECISION DEFAULT 0,
    spi_III_stow DOUBLE PRECISION DEFAULT 0,
    stow_total_ql INT DEFAULT 0,
    stow_total_time DOUBLE PRECISION DEFAULT 0,
    --PICK
    spi_I_pick DOUBLE PRECISION DEFAULT 0,
    spi_II_pick DOUBLE PRECISION DEFAULT 0,
    spi_III_pick DOUBLE PRECISION DEFAULT 0,
    pick_total_ql INT DEFAULT 0,
    pick_total_time DOUBLE PRECISION DEFAULT 0,
    --SORT
    spi_I_sort DOUBLE PRECISION DEFAULT 0,
    spi_II_sort DOUBLE PRECISION DEFAULT 0,
    spi_III_sort DOUBLE PRECISION DEFAULT 0,
    sort_total_ql INT DEFAULT 0,
    sort_total_time DOUBLE PRECISION DEFAULT 0,
    --SINGLE
    spi_I_single DOUBLE PRECISION DEFAULT 0,
    spi_II_single DOUBLE PRECISION DEFAULT 0,
    spi_III_single DOUBLE PRECISION DEFAULT 0,
    single_total_ql INT DEFAULT 0,
    single_total_time DOUBLE PRECISION DEFAULT 0,
    --MULTI
    spi_I_multi DOUBLE PRECISION DEFAULT 0,
    spi_II_multi DOUBLE PRECISION DEFAULT 0,
    spi_III_multi DOUBLE PRECISION DEFAULT 0,
    multi_total_ql INT DEFAULT 0,
    multi_total_time DOUBLE PRECISION DEFAULT 0,
    --LINESORTPACK
    spi_I_opt DOUBLE PRECISION DEFAULT 0,
    spi_II_opt DOUBLE PRECISION DEFAULT 0,
    spi_III_opt DOUBLE PRECISION DEFAULT 0,
    opt_total_ql INT DEFAULT 0,
    opt_total_time DOUBLE PRECISION DEFAULT 0,
    --INTERNAL ORDERS SHIPPING
    spi_I_wmo DOUBLE PRECISION DEFAULT 0,
    spi_II_wmo DOUBLE PRECISION DEFAULT 0,
    spi_III_wmo DOUBLE PRECISION DEFAULT 0,
    wmo_total_ql INT DEFAULT 0,
    wmo_total_time DOUBLE PRECISION DEFAULT 0,
    --REFURBISHMENT
    spi_I_return DOUBLE PRECISION DEFAULT 0,
    spi_II_return DOUBLE PRECISION DEFAULT 0,
    spi_III_return DOUBLE PRECISION DEFAULT 0,
    return_total_ql INT DEFAULT 0,
    return_total_time DOUBLE PRECISION DEFAULT 0
);

CREATE INDEX idx_spi_gd ON performance_gd.spi_gd (date);

CREATE TABLE IF NOT EXISTS performance_gd.spi_cluster_name(
    id SERIAL PRIMARY KEY,
    name VARCHAR(256) NOT NULL UNIQUE,
    description TEXT,
    department_id INT NOT NULL REFERENCES vision.department(id),
    user_id VARCHAR(512) NOT NULL
);

CREATE TABLE IF NOT EXISTS performance_gd.spi_cell(
    id BIGSERIAL PRIMARY KEY,
    name_id INT REFERENCES performance_gd.spi_cluster_name(id),
    cell DOUBLE PRECISION NOT NULL,
    valid_from DATE NOT NULL,
    user_id VARCHAR(512) NOT NULL
);

CREATE TABLE IF NOT EXISTS performance_gd.uph_process_name(
    id SERIAL PRIMARY KEY,
    name VARCHAR(256) NOT NULL UNIQUE,
    type VARCHAR (128) NOT NULL,
    department_id INT NOT NULL REFERENCES vision.department(id)
);

DO $$
DECLARE
    unloading_id INT;
    receive_id INT;
    sort_pack_id INT;
    pick_stow_id INT;
    shipping_id INT;
    return_id INT;
    quality_id INT;

    site_gd_id INT;
BEGIN
    SELECT id INTO site_gd_id FROM vision.site WHERE name = 'GD';
    SELECT id INTO unloading_id FROM vision.department WHERE name = 'Unloading' AND site_id = site_gd_id;
    SELECT id INTO receive_id FROM vision.department WHERE name = 'Receive' AND site_id = site_gd_id;
    SELECT id INTO sort_pack_id FROM vision.department WHERE name = 'Sort/Pack' AND site_id = site_gd_id;
    SELECT id INTO pick_stow_id FROM vision.department WHERE name = 'Pick/Stow' AND site_id = site_gd_id;
    SELECT id INTO shipping_id FROM vision.department WHERE name = 'Shipping' AND site_id = site_gd_id;
    SELECT id INTO return_id FROM vision.department WHERE name = 'Return' AND site_id = site_gd_id;
    SELECT id INTO quality_id FROM vision.department WHERE name = 'Quality' AND site_id = site_gd_id;

    INSERT INTO performance_gd.uph_process_name(name, department_id, type)
    VALUES
           ('Linesorter', sort_pack_id, 'Main'),
           ('Single', sort_pack_id, 'Main'),
           ('Multi', sort_pack_id, 'Main'),
           ('Sort', sort_pack_id, 'Main'),
           ('WMO', sort_pack_id, 'Main'),
           ('NCO', sort_pack_id, 'Additional'),
           ('Stow', pick_stow_id, 'Main'),
           ('Pick', pick_stow_id, 'Main'),
           ('Relocation', pick_stow_id, 'Additional'),
           ('Fastline', receive_id, 'Main'),
           ('ReceiveCore', receive_id, 'Main'),
           ('Rereplenishment', receive_id, 'Main'),
           ('Repackreceive', receive_id, 'Main'),
           ('Volumescan', receive_id, 'Additional'),
           ('Defect', return_id, 'Main'),
           ('Defect-Sort', return_id, 'Additional'),
           ('Stocktaking', quality_id, 'Additional');
END $$;

CREATE TABLE IF NOT EXISTS performance_gd.uph_process(
    id BIGSERIAL PRIMARY KEY,
    process_id INT REFERENCES performance_gd.uph_process_name(id),
    uph DOUBLE PRECISION NOT NULL,
    percent_others DOUBLE PRECISION NOT NULL,
    valid_from DATE NOT NULL,
    user_id VARCHAR(512) NOT NULL
);



CREATE TABLE IF NOT EXISTS performance_gd.color_performance(
    id SERIAL PRIMARY KEY,
    process_id INT REFERENCES performance_gd.uph_process_name(id),
    percent DOUBLE PRECISION NOT NULL,
    color VARCHAR(128) NOT NULL,
    user_id VARCHAR(512) NOT NULL
);