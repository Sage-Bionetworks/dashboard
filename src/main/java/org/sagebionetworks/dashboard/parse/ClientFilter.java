package org.sagebionetworks.dashboard.parse;

import java.util.regex.Pattern;

public class ClientFilter implements RecordFilter {

    private static final Pattern PATTERN_WEB = Pattern.compile(".*web-client.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_PYTHON = Pattern.compile(".*python.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_R = Pattern.compile(".*synapserclient.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_BRIDGE = Pattern.compile(".*bridge.*", Pattern.CASE_INSENSITIVE);

    public static final ClientFilter WEB = new ClientFilter(PATTERN_WEB);
    public static final ClientFilter PYTHON = new ClientFilter(PATTERN_PYTHON);
    public static final ClientFilter R = new ClientFilter(PATTERN_R);
    public static final ClientFilter BRIDGE = new ClientFilter(PATTERN_BRIDGE);

    @Override
    public boolean matches(AccessRecord record) {
        String userAgent = record.getUserAgent();
        if (userAgent == null) {
            return false;
        }
        return pattern.matcher(userAgent).matches();
    }

    private ClientFilter(Pattern pattern) {
        this.pattern = pattern;
    }

    private final Pattern pattern;
}
