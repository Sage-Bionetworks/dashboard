package org.sagebionetworks.dashboard.model;

public class DataPoint {

    public DataPoint(String value, String timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public String getValue() {
        return value;
    }

    public String getTimestamp() {
        return timestamp;
    }

    private final String value;
    private final String timestamp;
}
