CREATE TABLE IF NOT EXISTS access_record (
    object_id       varchar(12),
    entity_id       integer,
    elapse_ms       bigint          NOT NULL,
    timestamp       timestamp       NOT NULL,
    host            varchar(30),
    thread_id       integer         NOT NULL,
    user_agent      varchar(100),
    query           varchar(200),
    session_id      varchar(36)     PRIMARY KEY,
    request_url     varchar(100)    NOT NULL,
    user_id         integer         NOT NULL,
    method          varchar(10)     NOT NULL,
    vm_id           varchar(60),
    stack           integer         NOT NULL,
    instance        varchar(10),
    response_status    integer      NOT NULL
);

CREATE INDEX ON access_record USING btree(user_id);

CREATE INDEX ON access_record USING btree(timestamp);

CREATE INDEX ON access_record USING btree(entity_id);
