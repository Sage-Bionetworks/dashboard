package org.sagebionetworks.dashboard.dao;

/**
 * Handles the name-id mappings.
 */
public interface NameIdDao {

    /**
     * Gets the ID given a name.
     */
    String getId(String name, String nameIdKey, String idNameKey);

    /**
     * Gets the name given its ID.
     */
    String getName(String id, String idNameKey);
}
