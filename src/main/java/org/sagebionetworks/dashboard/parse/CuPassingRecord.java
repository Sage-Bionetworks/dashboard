package org.sagebionetworks.dashboard.parse;

import java.util.ArrayList;

/**
 * This class represent a Certified User Quiz Passing Record.
 */
public class CuPassingRecord {

    public CuPassingRecord(boolean isPassed, String userId, String timestamp, 
            int score, ArrayList<Responses>responseCorrectness) {

        this.isPassed = isPassed;
        this.userId = userId;
        this.timestamp = timestamp;
        this.score = score;
        this.responseCorrectness = responseCorrectness;
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
    public ArrayList<Responses> reponseCorrectness() {
        return responseCorrectness;
    }

    public class Responses {

        Responses(int questionIndex, boolean isCorrect) {
            this.questionIndex = questionIndex;
            this.isCorrect = isCorrect;
        }

        public int questionIndex(){
            return questionIndex;
        }
        public boolean isCorrect(){
            return isCorrect;
        }

        int questionIndex;
        boolean isCorrect;
    }

    private boolean isPassed;
    private String userId;
    private String timestamp;
    private int score;
    private ArrayList<Responses> responseCorrectness;
}
