package org.sagebionetworks.dashboard.parse;

import java.util.ArrayList;

/**
 * This class represent a Certified User Quiz Passing Record.
 */
public class CuPassingRecord {

    public CuPassingRecord(boolean isPassed, String userId, String timestamp, 
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
    public String timestamp() {
        return timestamp;
    }
    public int score() {
        return score;
    }
    public ArrayList<Response> reponseCorrectness() {
        return responses;
    }

    private boolean isPassed;
    private String userId;
    private String timestamp;
    private int score;
    private ArrayList<Response> responses;
}
