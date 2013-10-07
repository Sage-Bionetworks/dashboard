package org.sagebionetworks.dashboard.model.redis;

/**
 * Keys and parts of keys.
 */
public class Key {

    public static final String SEPARATOR = ":";

    /** Name --> ID mappings */
    public static final String NAME_ID = NameSpace.name + SEPARATOR + NameSpace.name;
    /** ID --> name mappings */
    public static final String ID_NAME = NameSpace.id + SEPARATOR + NameSpace.id;
}
