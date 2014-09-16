package org.sagebionetworks.dashboard.parse;

public interface AccessRecord {
    String getSessionId();
    Long getTimestamp();
    String getUserId();
    String getObjectId();
    String getMethod();
    String getUri();
    String getQueryString();
    String getStatus();
    Long getLatency();
    String getUserAgent();
    String getStack();
    String getHost();
}
