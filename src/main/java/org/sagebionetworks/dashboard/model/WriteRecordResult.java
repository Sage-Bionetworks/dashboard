package org.sagebionetworks.dashboard.model;

public class WriteRecordResult {

    public WriteRecordResult(boolean status, String metric, String file, int line) {
        this.status = status;
        this.metric = metric;
        this.file = file;
        this.line = line;
    }

    public boolean status() {
        return status;
    }

    public String metric() {
        return metric;
    }

    public String file() {
        return file;
    }

    public int line() {
        return line;
    }

    public String toString() {
        return metric + ":" + file + ":" + line + ":" + status;
    }

    private final boolean status;
    private final String file;
    private final String metric;
    private final int line;
}
