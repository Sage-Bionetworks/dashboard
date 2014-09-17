package org.sagebionetworks.dashboard.service;

import java.util.List;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.TimeSeriesDao;
import org.sagebionetworks.dashboard.metric.Metric;
import org.sagebionetworks.dashboard.parse.AccessRecord;
import org.springframework.stereotype.Service;

@Service("timeSeriesWriter")
public class TimeSeriesWriter extends AbstractMetricWriter<AccessRecord, Long> {

    @Resource
    private TimeSeriesDao timeSeriesDao;

    @Override
    void write(String metricId, String additionalKey, DateTime timestamp,
            Long value) {
        timeSeriesDao.put(metricId + additionalKey, timestamp, value);
    }

    @Override
    List<Metric<AccessRecord, Long>> getMetrics() {
        // TODO Auto-generated method stub
        return null;
    }

}
