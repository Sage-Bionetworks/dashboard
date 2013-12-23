package org.sagebionetworks.dashboard.metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sagebionetworks.dashboard.parse.ClientFilter;
import org.sagebionetworks.dashboard.parse.ClientReader;
import org.sagebionetworks.dashboard.parse.ProdFilter;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.springframework.stereotype.Component;

@Component("topWebClientMetric")
public class TopWebClientMetric implements UniqueCountMetric {

    @Override
    public String getName() {
        return "topWebClientMetric";
    }

    @Override
    public List<RecordFilter> getFilters() {
        return Collections.unmodifiableList(Arrays.asList(
                new ProdFilter(), ClientFilter.WEB));
    }

    @Override
    public RecordReader<String> getRecordReader() {
        return new ClientReader();
    }
}
