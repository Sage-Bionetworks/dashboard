package org.sagebionetworks.dashboard.parse;

/**
 * This class represents a user's response.
 */
public class ResponseCorrectness {

    public ResponseCorrectness(QuestionResponse response, boolean isCorrect, Question question) {
        this.response = response;
        this.isCorrect = isCorrect;
        this.question = question;
    }

    public QuestionResponse response() {
        return response;
    }
    public boolean isCorrect() {
        return isCorrect;
    }
    public Question question() {
        return question;
    }

    private QuestionResponse response;
    private boolean isCorrect;
    private Question question;
}
