CREATE TABLE IF NOT EXISTS file_status (
    file_path   varchar(100)    PRIMARY KEY,
    status      varchar(10)     NOT NULL
);

CREATE INDEX ON file_status USING btree(status);
