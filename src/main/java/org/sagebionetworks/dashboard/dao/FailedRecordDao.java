package org.sagebionetworks.dashboard.dao;

import org.sagebionetworks.dashboard.model.WriteRecordResult;

public interface FailedRecordDao {

    void put(WriteRecordResult result);
}
