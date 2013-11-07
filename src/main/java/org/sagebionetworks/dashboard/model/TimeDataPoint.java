package org.sagebionetworks.dashboard.model;

public class TimeDataPoint implements ValuePair<String>{

    public TimeDataPoint(long timestampInMs, String value) {
        this.timestamp = timestampInMs;
        this.value = value;
    }

    @Override
    public String getX() {
        return Long.toString(timestamp);
    }

    @Override
    public String getY() {
        return value;
    }

    public long getTimestampInMs() {
        return timestamp;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "TimeDataPoint [timestamp=" + timestamp + ", value=" + value
                + "]";
    }

    private final long timestamp;
    private final String value;
}
