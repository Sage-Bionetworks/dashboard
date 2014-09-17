package org.sagebionetworks.dashboard.parse;

public class QuestionFailFilter implements RecordFilter<CuResponseRecord>{

    @Override
    public boolean matches(CuResponseRecord record) {
        return !record.isCorrect();
    }
}
