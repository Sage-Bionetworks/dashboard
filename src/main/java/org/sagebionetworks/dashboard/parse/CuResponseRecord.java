package org.sagebionetworks.dashboard.parse;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.model.Record;

public class CuResponseRecord implements Record {

    public CuResponseRecord(int responseId, int questionIndex, DateTime timestamp, boolean isCorrect) {
        this.responseId = responseId;
        this.questionIndex = questionIndex;
        this.timestamp = timestamp;
        this.isCorrect = isCorrect;
    }
    @Override
    public Long getTimestamp() {
        return timestamp.getMillis();
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
