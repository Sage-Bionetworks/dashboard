package org.sagebionetworks.dashboard.parse;

public class UriSearchFilter implements RecordFilter {
    @Override
    public boolean matches(AccessRecord record) {
        String uri = record.getUri();
        return "/repo/v1/search".equalsIgnoreCase(uri);
    }
}
