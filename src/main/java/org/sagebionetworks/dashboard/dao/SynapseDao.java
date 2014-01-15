package org.sagebionetworks.dashboard.dao;

/**
 * Caches data retrieved directly from Synapse.
 */
public interface SynapseDao {

    /** If the user is a dashboard user. */
    boolean isDashboardUser(String email);

    /** Gets user display name. Can return null. */
    String getUserDisplayName(String userId);

    /** Gets entity name. Can return null. */
    String getEntityName(String entityId);
}
