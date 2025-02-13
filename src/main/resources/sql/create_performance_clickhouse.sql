CREATE TABLE performance (
    date Date,
    expertis String,
    activity_name String,
    spi_cluster String,
    final_cluster String,
    start_activity DateTime,
    end_activity DateTime,
    duration Float32,
    ql UInt16,
    ql_box UInt16,
    ql_hanging UInt16,
    ql_shoes UInt16,
    ql_boots UInt16,
    ql_other UInt16,
    stow_clarifications UInt16,
    pick_nos1 UInt16,
    pick_nos2 UInt16

)
    ENGINE = MergeTree
PARTITION BY toYYYYMM(date)
ORDER BY (date, expertis, activity_name)
SETTINGS index_granularity = 8192;