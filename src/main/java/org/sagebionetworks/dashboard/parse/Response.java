package org.sagebionetworks.dashboard.parse;

public class Response {

    public Response(int responseId, int questionIndex, boolean isCorrect) {
        this.responseId = responseId;
        this.questionIndex = questionIndex;
        this.isCorrect = isCorrect;
    }

    public int responseId(){
        return responseId;
    }
    public int questionIndex(){
        return questionIndex;
    }
    public boolean isCorrect(){
        return isCorrect;
    }

    private final int responseId;
    private final int questionIndex;
    private final boolean isCorrect;
}
