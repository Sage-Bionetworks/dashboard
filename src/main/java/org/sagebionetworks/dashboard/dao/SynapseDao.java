package org.sagebionetworks.dashboard.dao;

/**
 * Caches data retrieved directly from Synapse.
 */
public interface SynapseDao {

    /** Gets user display name. Can return null. */
    String getUserDisplayName(String userId);

    /** Gets entity name. Can return null. */
    String getEntityName(String entityId);
}
