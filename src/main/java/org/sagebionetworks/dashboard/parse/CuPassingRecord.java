package org.sagebionetworks.dashboard.parse;

import java.util.ArrayList;

import org.joda.time.DateTime;

/**
 * This class represent a Certified User Quiz Passing Record.
 */
public class CuPassingRecord {

    public CuPassingRecord(boolean isPassed, String userId, DateTime timestamp, 
            int score, ArrayList<Response>responseCorrectness) {

        this.isPassed = isPassed;
        this.userId = userId;
        this.timestamp = timestamp;
        this.score = score;
        this.responses = responseCorrectness;
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
    public ArrayList<Response> responses() {
        return responses;
    }

    private boolean isPassed;
    private String userId;
    private DateTime timestamp;
    private int score;
    private ArrayList<Response> responses;
}
