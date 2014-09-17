package org.sagebionetworks.dashboard.service;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.TimeSeriesDao;
import org.sagebionetworks.dashboard.parse.AccessRecord;
import org.springframework.stereotype.Service;

@Service("timeSeriesWriter")
public class TimeSeriesWriter extends AbstractMetricWriter<AccessRecord, Long> {

    @Override
    void write(String metricId, DateTime timestamp, Long value) {
        timeSeriesDao.put(metricId, timestamp, value);
    }

    @Resource
    private TimeSeriesDao timeSeriesDao;

    @Override
    void write(String metricId, String additionalKey, DateTime timestamp,
            Long value) {
        timeSeriesDao.put(metricId + additionalKey, timestamp, value);
    }

}
