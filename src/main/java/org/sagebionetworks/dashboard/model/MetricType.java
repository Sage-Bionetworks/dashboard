package org.sagebionetworks.dashboard.model;

public enum MetricType {

    /**
     * Time series with simple statistics (n, sum, avg, max).
     */
    TIME_SERIES,

    /**
     * Count of unique IDs. For example, daily unique users.
     */
    UNIQUE_COUNT,

    /**
     * Counts of unique IDs in reverse order.  For example, daily top users, top entities, etc. 
     */
    TOP
}
