CREATE TABLE IF NOT EXISTS access_record (
    object_id       varchar(12),
    entity_id       integer,
    elapse_ms       bigint          NOT NULL,
    timestamp       bigint       NOT NULL,
    host            varchar(100),
    thread_id       integer         NOT NULL,
    user_agent      varchar(100),
    query           text,
    session_id      varchar(100)     PRIMARY KEY,
    request_url     varchar(100)    NOT NULL,
    user_id         integer,
    method          varchar(10)     NOT NULL,
    vm_id           varchar(60),
    stack           varchar(10),
    instance        integer         NOT NULL,
    response_status    integer      NOT NULL
);

CREATE INDEX ON access_record USING btree(user_id);

CREATE INDEX ON access_record USING btree(timestamp);

CREATE INDEX ON access_record USING btree(entity_id);
