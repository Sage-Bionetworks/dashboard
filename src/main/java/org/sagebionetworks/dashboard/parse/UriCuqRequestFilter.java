package org.sagebionetworks.dashboard.parse;

import org.sagebionetworks.dashboard.model.AccessRecord;

public class UriCuqRequestFilter implements RecordFilter<AccessRecord> {
    @Override
    public boolean matches(AccessRecord record) {
        String uri = record.getUri();
        return "/repo/v1/certifiedUserTest".equalsIgnoreCase(uri);
    }
}
