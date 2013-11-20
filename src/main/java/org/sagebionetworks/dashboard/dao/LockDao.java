package org.sagebionetworks.dashboard.dao;

public interface LockDao {

    /**
     * Tries to acquire the lock. If it succeeds, returns the etag associated
     * with the lock. If it fails, return null. Note the lock will be acquired with
     * the default timeout.
     *
     * @param lock The name of the lock
     * @return etag of the acquired lock or null if failed to acquire the lock
     */
    String acquire(String lock);

    /**
     * Releases the acquired lock.
     *
     * @param lock  The name of the lock.
     * @param etag  The etag associated with the acquired lock.
     * @return true if successfully released;
     *         false if release fails (e.g. not holding the lock anymore).
     */
    boolean release(String lock, String etag);
}
