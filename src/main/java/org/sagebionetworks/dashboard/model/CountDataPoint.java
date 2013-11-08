package org.sagebionetworks.dashboard.model;

public class CountDataPoint implements ValuePair<String> {

    public CountDataPoint(String id, long count) {
        this.id = id;
        this.count = count;
    }

    @Override
    public String getX() {
        return id;
    }

    @Override
    public String getY() {
        return Long.toString(count);
    }

    public String getId() {
        return id;
    }

    public long getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "CountDataPoint [id=" + id + ", count=" + count + "]";
    }

    private final String id;
    private final long count;
}
