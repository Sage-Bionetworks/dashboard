package org.sagebionetworks.dashboard.dao.redis;

enum NameSpace {

    /** The set of names. */
    name,

    /** The set of IDs. */
    id,

    /** The set of files. */
    file,

    /** Cached Synapse data. */
    synapse,

    /** Users. */
    user,

    /** User emails. */
    email,

    /** Time series metrics. */
    timeseries,

    /** Counts by unique IDs. */
    uniquecount,

    /** Count of bits. */
    bitcount,

    /** Simple counts. */
    count
}
