package org.sagebionetworks.dashboard.parse;

import java.util.ArrayList;

/**
 * This class represent a Certified User Quiz Passing Record.
 * The fields in this class coresponse to the resulting fields in the REST API:
 * GET /user/{id}/certifiedUserPassingRecord
 */
public class CuPassingRecord {

    public CuPassingRecord(int responseId, boolean status, String userId,
            String timestamp, int score, 
            ArrayList<ResponseCorrectness>responseCorrectness, int quizId) {
        this.responseId = responseId;
        this.status = status;
        this.userId = userId;
        this.timestamp = timestamp;
        this.score = score;
        this.responseCorrectness = responseCorrectness;
        this.quizId = quizId;
    }

    public int getResponseId() {
        return responseId;
    }
    public boolean getStatus() {
        return status;
    }
    public String getUserId() {
        return userId;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public int getScore() {
        return score;
    }
    public ArrayList<ResponseCorrectness> getReponseCorrectness() {
        return responseCorrectness;
    }
    public int getQuizId() {
        return quizId;
    }

    private int responseId;
    private boolean status;
    private String userId;
    private String timestamp;
    private int score;
    private ArrayList<ResponseCorrectness> responseCorrectness;
    private int quizId;
}
