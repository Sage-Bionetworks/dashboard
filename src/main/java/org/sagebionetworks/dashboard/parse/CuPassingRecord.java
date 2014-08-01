package org.sagebionetworks.dashboard.parse;

import java.util.ArrayList;

/**
 * This class represent a Certified User Quiz Passing Record.
 * The fields in this class coresponse to the resulting fields in the REST API:
 * GET /user/{id}/certifiedUserPassingRecord
 */
public class CuPassingRecord {

    public CuPassingRecord(int responseId, boolean isPassed, String userId,
            String timestamp, int score, 
            ArrayList<ResponseCorrectness>responseCorrectness, int quizId) {
        this.responseId = responseId;
        this.isPassed = isPassed;
        this.userId = userId;
        this.timestamp = timestamp;
        this.score = score;
        this.responseCorrectness = responseCorrectness;
        this.quizId = quizId;
    }

    public int responseId() {
        return responseId;
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
    public ArrayList<ResponseCorrectness> reponseCorrectness() {
        return responseCorrectness;
    }
    public int quizId() {
        return quizId;
    }

    private int responseId;
    private boolean isPassed;
    private String userId;
    private String timestamp;
    private int score;
    private ArrayList<ResponseCorrectness> responseCorrectness;
    private int quizId;
}
