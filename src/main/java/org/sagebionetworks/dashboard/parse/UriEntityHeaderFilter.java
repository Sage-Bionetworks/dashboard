package org.sagebionetworks.dashboard.parse;

public class UriEntityHeaderFilter implements RecordFilter {
    @Override
    public boolean matches(Record record) {
        String uri = record.getUri();
        return "/repo/v1/entity/header".equalsIgnoreCase(uri);
    }
}
