package org.sagebionetworks.dashboard.parse;

public class UriCuqSubmitFilter implements RecordFilter {
    @Override
    public boolean matches(Record record) {
        String uri = record.getUri();
        return "/repo/v1/certifiedUserTestResponse".equalsIgnoreCase(uri);
    }
}
