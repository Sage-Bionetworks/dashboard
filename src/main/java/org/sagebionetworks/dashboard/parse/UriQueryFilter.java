package org.sagebionetworks.dashboard.parse;

public class UriQueryFilter implements RecordFilter {
    @Override
    public boolean matches(AccessRecord record) {
        String uri = record.getUri();
        return "/repo/v1/query".equalsIgnoreCase(uri);
    }
}
