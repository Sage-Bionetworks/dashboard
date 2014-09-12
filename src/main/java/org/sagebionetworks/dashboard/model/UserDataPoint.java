package org.sagebionetworks.dashboard.model;

public class UserDataPoint {

    public UserDataPoint(String key) {
        String[] tokens = key.split(":");
        this.timestamp = tokens[0];
        this.userId = tokens[1];
        this.client = tokens[2];
    }

    public String userId() {
        return this.userId;
    }

    public String timestamp() {
        return this.timestamp;
    }

    public String client() {
        return this.client;
    }

    public String toString() {
        return this.timestamp + ":" + this.userId + ":" + this.client;
    }

    private final String userId;
    private final String timestamp;
    private final String client;
}
