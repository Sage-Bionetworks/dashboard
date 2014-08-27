package org.sagebionetworks.dashboard.parse;

import org.joda.time.DateTime;

public class Response {

    public Response(int responseId, int questionIndex, DateTime timestamp, boolean isCorrect) {
        this.responseId = responseId;
        this.questionIndex = questionIndex;
        this.timestamp = timestamp;
        this.isCorrect = isCorrect;
    }

    public int responseId(){
        return responseId;
    }
    public int questionIndex(){
        return questionIndex;
    }
    public DateTime timestamp() {
        return timestamp;
    }
    public boolean isCorrect(){
        return isCorrect;
    }

    private final int responseId;
    private final int questionIndex;
    private final DateTime timestamp;
    private final boolean isCorrect;
}
