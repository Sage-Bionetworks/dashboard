package org.sagebionetworks.dashboard.model;


public class UserDataPoint {

    public UserDataPoint(String key, long value) {
        String[] tokens = key.split(":");
        this.timestamp = tokens[0];
        this.userId = tokens[1];
        this.client = tokens[2];
        this.nodownload = value;
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

    public String numberOfDownload() {
        return Long.toString(nodownload);
    }

    public String toString() {
        return this.timestamp + ":" + this.userId + ":" + this.client +
                ":" + Long.toString(nodownload);
    }

    private String userId;
    private String timestamp;
    private String client;
    private long nodownload;
}
