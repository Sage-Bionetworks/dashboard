package org.sagebionetworks.dashboard.model.redis;

/**
 * Keys and parts of keys.
 */
public class RedisKey {

    public static final String SEPARATOR = ":";

    public static class Statistic {
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
        /** The set of names. */
        public static final String NAME = "name";
        /** The set of IDs. */
        public static final String ID = "id";
        /** Synapse clients. */
        public static final String CLIENT = "c";
    }

    /** Name --> ID mappings */
    public static final String NAME_ID = NameSpace.NAME + SEPARATOR + NameSpace.ID;
    /** ID --> name mappings */
    public static final String ID_NAME = NameSpace.ID + SEPARATOR + NameSpace.NAME;
}
