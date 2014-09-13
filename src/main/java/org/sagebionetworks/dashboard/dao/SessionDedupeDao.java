package org.sagebionetworks.dashboard.dao;

public interface SessionDedupeDao {

    /**
     * Whether the session has already been processed.
     */
    boolean isProcessed(String sessionId);
}
