package org.sagebionetworks.dashboard.parse;

import org.sagebionetworks.dashboard.model.AccessRecord;

// TODO: This does not distinguish prod from staging
public class ProdFilter implements RecordFilter<AccessRecord> {
    @Override
    public boolean matches(AccessRecord record) {
        return "prod".equalsIgnoreCase(record.getStack());
    }
}
