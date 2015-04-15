package org.sagebionetworks.dashboard.parse;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.model.Record;

/**
 * This class represent a Certified User Quiz Passing Record.
 */
public class CuPassingRecord implements Record {

    public CuPassingRecord(boolean isPassed, String userId, DateTime timestamp, int score) {

        this.isPassed = isPassed;
        this.userId = userId;
        this.timestamp = timestamp;
        this.score = score;
    }

    @Override
    public Long getTimestamp() {
        return timestamp.getMillis();
    }

    public boolean isPassed() {
        return isPassed;
    }
    public String userId() {
        return userId;
    }
    public DateTime timestamp() {
        return timestamp;
    }
    public int score() {
        return score;
    }

    private final boolean isPassed;
    private final String userId;
    private final DateTime timestamp;
    private final int score;
}
