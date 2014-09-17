package org.sagebionetworks.dashboard.service;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.FileDownloadDao;
import org.sagebionetworks.dashboard.dao.NameIdDao;
import org.sagebionetworks.dashboard.metric.Metric;
import org.sagebionetworks.dashboard.parse.AccessRecord;
import org.sagebionetworks.dashboard.parse.RecordFilter;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.springframework.stereotype.Service;

//@Service("reportWriter")
public class ReportWriter implements MetricWriter<AccessRecord, String>{

    @Override
    public void writeMetric(final AccessRecord record, final Metric<AccessRecord, String> metric) {
        writeMetric(record, metric, Collections.<RecordFilter<AccessRecord>> emptyList(), "");
    }

    public void writeMetric(AccessRecord record, Metric<AccessRecord, String> metric,
            List<RecordFilter<AccessRecord>> additionalFilters, String additionalKey) {
        // Apply the filters first
        List<RecordFilter<AccessRecord>> filters = metric.getFilters();
        for (RecordFilter<AccessRecord> filter : filters) {
            if (!filter.matches(record)) {
                return;
            }
        }
        for (RecordFilter<AccessRecord> filter : additionalFilters) {
            if (!filter.matches(record)) {
                return;
            }
        }

        // Read the record
        final String metricName = metric.getName();
        final String metricId = nameIdDao.getId(metricName);
        final Long t = record.getTimestamp();
        final DateTime timestamp = new DateTime(t.longValue());
        final RecordReader<AccessRecord, String> reader = metric.getRecordReader();
        final String value = reader.read(record);
        final String entityId = getEntityId(record.getUri());

        // Write the metric
        if (value != null) {
            write(metricId, timestamp, entityId, value);
        }
    }

    void write(String metricId, DateTime timestamp, String entityId, String userData) {
        //fileDownloadDao.put(metricId, entityId, timestamp, userData);
    }

    //@Resource
    //private FileDownloadDao fileDownloadDao;

    @Resource
    private NameIdDao nameIdDao;

    // returns the entityId from a download Uri
    private String getEntityId(String uri) {
        Pattern pattern = Pattern.compile("/entity/(.*?)/");
        Matcher matcher = pattern.matcher(uri);
        String res = "";
        if (matcher.find()) {
            res = matcher.group(1);
        }
        if (res.startsWith("syn")) {
            res = res.substring(3);
        }
        return res;
    }

    @Override
    public void writeMetric(AccessRecord record,
            Metric<AccessRecord, String> metric, String additionalKey) {
        // TODO Auto-generated method stub
        
    }

}
