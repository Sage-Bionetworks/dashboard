CREATE TABLE IF NOT EXISTS record_status (
    file_path   varchar(100)    PRIMARY KEY,
    line_number integer         NOT NULL,
    metric      varchar(100)    NOT NULL,
    status      varchar(10)     NOT NULL
);

CREATE INDEX ON record_status USING btree(status);

CREATE INDEX ON record_status USING btree(metric);
