package org.sagebionetworks.dashboard.dao.redis;

class RedisConstants {

    /** By default, we cache the metrics for 180 days. */
    static final long EXPIRE_DAYS = 180L;

    /** Lock on file processing is set as 30 minutes. */
    static final long LOCK_EXPIRE_MINUTES = 30L;
}
