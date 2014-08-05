package org.sagebionetworks.dashboard.parse;

public class QuestionFailFilter implements RecordFilter{

    @Override
    public boolean matches(Record record) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean matches(Response record) {
        return !record.isCorrect();
    }
}
