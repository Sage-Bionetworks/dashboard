package org.sagebionetworks.dashboard.parse;

public class QuestionIndexReader implements RecordReader<String> {

    @Override
    public String read(Record record) {
        // TODO Auto-generated method stub
        return null;
    }

    public String read(Response record) {
        return Integer.toString(record.questionIndex());
    }
}
