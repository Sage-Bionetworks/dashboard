package org.sagebionetworks.dashboard.model;

public class TimeDataPoint {

    public TimeDataPoint(String timestamp, String value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getValue() {
        return value;
    }

    private final String timestamp;
    private final String value;
}
