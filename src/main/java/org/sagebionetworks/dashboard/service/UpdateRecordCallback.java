package org.sagebionetworks.dashboard.service;

import org.sagebionetworks.dashboard.model.WriteRecordResult;

public interface UpdateRecordCallback {

    void handle(WriteRecordResult result);
}
