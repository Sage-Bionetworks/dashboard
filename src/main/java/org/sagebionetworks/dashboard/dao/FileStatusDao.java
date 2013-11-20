package org.sagebionetworks.dashboard.dao;

import java.util.List;

import org.sagebionetworks.dashboard.model.FileFailure;

/**
 * Keeps track of file status.
 */
public interface FileStatusDao {

    /**
     * If the file has been successfully processed.
     */
    boolean isCompleted(String file);

    /**
     * Sets the file to be successfully processed. If the file is already marked
     * as failed, it will not any more.
     */
    void setCompleted(String file);

    /**
     * If the files has failed.
     */
    boolean isFailed(String file);

    /**
     * Sets the file as failed and records the line number (1-based, inclusive)
     * where the failure occurred.
     */
    void setFailed(String file, int lineNumber);

    /**
     * Gets the list of failures.
     */
    List<FileFailure> getFailures();
}
