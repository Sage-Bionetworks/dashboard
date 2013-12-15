package org.sagebionetworks.dashboard.metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sagebionetworks.dashboard.parse.EntityIdReader;
import org.sagebionetworks.dashboard.parse.ProdFilter;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.springframework.stereotype.Component;

@Component("topEntityMetric")
public class TopEntityMetric implements UniqueCountToWrite {

    @Override
    public String getName() {
        return "topEntityMetric";
    }

    @Override
    public List<RecordFilter> getFilters() {
        return Collections.unmodifiableList(Arrays.asList(
                (RecordFilter)new ProdFilter()));
    }

    @Override
    public RecordReader<String> getRecordReader() {
        return new EntityIdReader();
    }
}
