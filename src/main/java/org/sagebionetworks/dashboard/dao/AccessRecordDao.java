package org.sagebionetworks.dashboard.dao;

import org.sagebionetworks.dashboard.parse.AccessRecord;

public interface AccessRecordDao {

    void put(AccessRecord record);
}
