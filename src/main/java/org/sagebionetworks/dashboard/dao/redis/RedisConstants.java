package org.sagebionetworks.dashboard.dao.redis;

class RedisConstants {

    /** By default, we cache the metrics for 200 days. */
    static final int EXPIRE_DAYS = 200;

    /** Lock on file processing is set as 30 minutes. */
    static final long LOCK_EXPIRE_MINUTES = 30L;
}
