package org.sagebionetworks.dashboard.dao;

import java.util.List;

import org.sagebionetworks.dashboard.model.FileFailure;

/**
 * Keeps track of file status.
 */
public interface FileStatusDao {

    boolean isCompleted(String file);

    void setCompleted(String file);

    boolean isFailed(String file);

    void setFailed(String file, int lineNumber);

    List<FileFailure> getFailures();
}
