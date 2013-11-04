package org.sagebionetworks.dashboard.model;

public class CountDataPoint {

    public CountDataPoint(String id, String count) {
        this.id = id;
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public String getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "CountDataPoint [id=" + id + ", count=" + count + "]";
    }

    private final String id;
    private final String count;
}
