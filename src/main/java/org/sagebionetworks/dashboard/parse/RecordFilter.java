package org.sagebionetworks.dashboard.parse;

import org.sagebionetworks.dashboard.model.Record;

public interface RecordFilter<R extends Record> {
    boolean matches(R record);
}
