package org.sagebionetworks.dashboard.parse;

public interface Record {
    String getSessionId();
    Long getTimestamp();
    String getUserId();
    String getObjectId();
    String getMethod();
    String getUri();
    String getStatus();
    Long getLatency();
    String getUserAgent();
    String getStack();
    String getHost();
}
