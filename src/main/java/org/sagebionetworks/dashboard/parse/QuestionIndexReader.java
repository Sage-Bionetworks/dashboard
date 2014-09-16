package org.sagebionetworks.dashboard.parse;

public class QuestionIndexReader implements RecordReader<String> {

    @Override
    public String read(AccessRecord record) {
        throw new RuntimeException("Method is not supported.");
    }

    public String read(Response record) {
        return Integer.toString(record.questionIndex());
    }
}
