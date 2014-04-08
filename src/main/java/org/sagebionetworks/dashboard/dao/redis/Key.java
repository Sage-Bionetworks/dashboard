package org.sagebionetworks.dashboard.dao.redis;

/**
 * Keys and parts of keys.
 */
class Key {

    static final String SEPARATOR = ":";

    /** Name --> ID mappings */
    static final String NAME_ID = NameSpace.name + SEPARATOR + NameSpace.id;
    /** ID --> name mappings */
    static final String ID_NAME = NameSpace.id + SEPARATOR + NameSpace.name;

    /** The set of completed log files. */
    static final String FILE_COMPLETED = NameSpace.file + SEPARATOR + "completed";
    /** The set of failed log files. */
    static final String FILE_FAILED = NameSpace.file + SEPARATOR + "failed";

    /** Synapse session token.*/
    static final String SYNAPSE_SESSION = NameSpace.synapse + SEPARATOR + "session";
    /** Synapse user id -> name map. */
    static final String SYNAPSE_USER_ID_NAME = NameSpace.synapse + SEPARATOR + NameSpace.user + SEPARATOR + ID_NAME;
    /** Synapse user id -> email map. */
    static final String SYNAPSE_USER_ID_EMAIL = NameSpace.synapse + SEPARATOR + NameSpace.user + SEPARATOR + NameSpace.id + SEPARATOR + NameSpace.email;
    /** Synapse user email -> id map. */
    static final String SYNAPSE_USER_EMAIL_ID = NameSpace.synapse + SEPARATOR + NameSpace.user + SEPARATOR + NameSpace.email + SEPARATOR + NameSpace.id;
    /** Synapse entity id -> name map. */
    static final String SYNAPSE_ENTITY_ID_NAME = NameSpace.synapse + SEPARATOR + "entity" + SEPARATOR + ID_NAME;
    /** Synapse entity id -> benefactor id map. */
    static final String SYNAPSE_ENTITY_BENEFACTOR = NameSpace.synapse + SEPARATOR + "entity" + SEPARATOR + "benefactor";
}
