package org.sagebionetworks.dashboard.parse;

public class QuestionFailFilter implements RecordFilter{

    @Override
    public boolean matches(AccessRecord record) {
        throw new RuntimeException("Method is not supported.");
    }

    public boolean matches(Response record) {
        return !record.isCorrect();
    }
}
