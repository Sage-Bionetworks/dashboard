package org.sagebionetworks.dashboard.model;

import org.sagebionetworks.dashboard.dao.redis.Key;

public class UserDataPoint {

    public UserDataPoint(String key, long value) {
        String[] tokens = key.split(Key.SEPARATOR);
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
        return this.timestamp + Key.SEPARATOR + this.userId + Key.SEPARATOR + 
                this.client + Key.SEPARATOR + Long.toString(nodownload);
    }

    private String userId;
    private String timestamp;
    private String client;
    private long nodownload;
}
