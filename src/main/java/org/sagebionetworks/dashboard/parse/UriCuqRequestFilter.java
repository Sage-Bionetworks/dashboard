package org.sagebionetworks.dashboard.parse;

public class UriCuqRequestFilter implements RecordFilter {
    @Override
    public boolean matches(AccessRecord record) {
        String uri = record.getUri();
        return "/repo/v1/certifiedUserTest".equalsIgnoreCase(uri);
    }
}
