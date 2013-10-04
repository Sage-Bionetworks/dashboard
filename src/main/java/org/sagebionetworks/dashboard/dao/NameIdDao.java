package org.sagebionetworks.dashboard.dao;

/**
 * Handles the name-ID mappings. The purpose here is to use a much shorter ID
 * to represent a much longer name within Redis.
 */
public interface NameIdDao {

    /**
     * Gets the ID given a name. If the name does not exist yet, a new ID
     * will be generated.
     */
    String getId(String name);

    /**
     * Gets the name given its ID. Returns null when the ID does not exist.
     */
    String getName(String id);
}
