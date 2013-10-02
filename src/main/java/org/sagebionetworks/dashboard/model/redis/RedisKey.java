package org.sagebionetworks.dashboard.model.redis;

/**
 * Keys and parts of keys.
 */
public class RedisKey {

    public static final String SEPARATOR = ":";

    public static class Metric {
        public static final String N = "n";
        public static final String SUM = "s";
        public static final String AVG = "a";
        public static final String MAX = "m";
    }

    public static class Aggregation {
        public static final String MINUTE3 = "m3";
        public static final String HOUR = "h";
        public static final String DAY = "d";
    }

    public static class NameSpace {
        /** The set of IDs. */
        public static final String ID = "id";
        /** Synapse clients. */
        public static final String CLIENT = "c";
    }

    /** The set of IDs. */
    public static final String ID = NameSpace.ID;

    /** Client name --> ID mappings */
    public static final String CLIENT_ID = NameSpace.CLIENT + SEPARATOR + NameSpace.ID;
    /** Client ID --> name mappings */
    public static final String ID_CLIENT = NameSpace.ID + SEPARATOR + NameSpace.CLIENT;
}
