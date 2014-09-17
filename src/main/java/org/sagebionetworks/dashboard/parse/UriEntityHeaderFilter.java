package org.sagebionetworks.dashboard.parse;

public class UriEntityHeaderFilter implements RecordFilter<AccessRecord> {
    @Override
    public boolean matches(AccessRecord record) {
        String uri = record.getUri();
        return "/repo/v1/entity/header".equalsIgnoreCase(uri);
    }
}
