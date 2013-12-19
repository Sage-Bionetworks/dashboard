package org.sagebionetworks.dashboard.parse;

public class ClientSummaryReader implements RecordReader<String> {

    @Override
    public String read(Record record) {
        if (ClientFilter.PYTHON.matches(record)) {
            return "Python Client";
        }
        if (ClientFilter.R.matches(record)) {
            return "R Client";
        }
        if (ClientFilter.WEB.matches(record)) {
            return "Web Client";
        }
        return clientReader.read(record);
    }

    private final RecordReader<String> clientReader = new ClientReader();
}
