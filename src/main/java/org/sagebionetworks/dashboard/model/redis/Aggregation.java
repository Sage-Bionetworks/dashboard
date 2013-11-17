package org.sagebionetworks.dashboard.model.redis;

public enum Aggregation {

    /** Aggregates every 3 minutes. */
    m3,

    /** Aggregates hourly. */
    hour,

    /** Aggregates daily. */
    day
}
