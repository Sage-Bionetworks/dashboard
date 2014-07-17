package org.sagebionetworks.dashboard.model;

import org.joda.time.DateTime;

public class UserDataPoint {

    public UserDataPoint(String userId, String timestamp, String client) {
        this.userId = userId;
        this.timestamp = timestamp;
        this.client = client;
    }

    public UserDataPoint(String userId, DateTime timestamp, String client) {
        this.userId = userId;
        this.timestamp = timestamp.toString();
        this.client = client;
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

    private String userId;
    private String timestamp;
    private String client;
}
