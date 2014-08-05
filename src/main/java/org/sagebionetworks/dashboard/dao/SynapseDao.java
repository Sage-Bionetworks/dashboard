package org.sagebionetworks.dashboard.dao;

import java.util.List;

import org.sagebionetworks.dashboard.parse.CuPassingRecord;

/**
 * Caches data retrieved directly from Synapse.
 */
public interface SynapseDao {

    /** Gets user display name. Can return null. */
    String getUserName(String userId);

    /** Gets entity name. Can return null. */
    String getEntityName(String entityId);

    /** Gets the ID of the benefactor. */
    String getBenefactor(String entityId);

    /** Gets the ID of the containing project. */
    String getProject(String entityId);

    /** Gets the list of user names given IDs. */
    List<String> getUserNames(List<String> userIds);

    /** Gets the list of entity names given IDs. */
    List<String> getEntityNames(List<String> entityIds);

    /** Gets the list of IDs of the containing projects given entity IDs. */
    List<String> getProjects(List<String> entityIds);

    /** Gets the PassingRecord of a user's Certified Quiz */
    CuPassingRecord getCuPassingRecord(String userId);

    /** Refresh the list of users. */
    void refreshUsers();
}
