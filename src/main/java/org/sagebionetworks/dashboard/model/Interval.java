package org.sagebionetworks.dashboard.model;

/**
 * Different sizes of the time bins within which numbers are aggregated.
 */
public enum Interval {

    /** Aggregates every 3 minutes. */
    m3,

    /** Aggregates hourly. */
    hour,

    /** Aggregates daily. */
    day
}
