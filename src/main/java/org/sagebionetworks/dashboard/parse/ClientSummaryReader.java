package org.sagebionetworks.dashboard.parse;

import org.sagebionetworks.dashboard.model.AccessRecord;

public class ClientSummaryReader implements RecordReader<AccessRecord, String> {

    @Override
    public String read(AccessRecord record) {
        if (ClientFilter.PYTHON.matches(record)) {
            return "Python Client";
        }
        if (ClientFilter.R.matches(record)) {
            return "R Client";
        }
        if (ClientFilter.WEB.matches(record)) {
            return "Web Client";
        }
        if (ClientFilter.BRIDGE.matches(record)) {
            return "Bridge Client";
        }
        return clientReader.read(record);
    }

    private final RecordReader<AccessRecord, String> clientReader = new ClientReader();
}
