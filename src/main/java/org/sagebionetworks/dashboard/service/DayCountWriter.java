package org.sagebionetworks.dashboard.service;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.UniqueCountDao;
import org.sagebionetworks.dashboard.parse.AccessRecord;
import org.springframework.stereotype.Service;

@Service("dayCountWriter")
public class DayCountWriter extends AbstractMetricWriter<AccessRecord, String> {

    @Override
    void write(String metricId, DateTime timestamp, String id) {
        dayCountDao.put(metricId, id, timestamp);
    }

    @Resource
    private UniqueCountDao dayCountDao;

    @Override
    void write(String metricId, String additionalKey, DateTime timestamp,
            String id) {
        dayCountDao.put(metricId + additionalKey, id, timestamp);
    }
}
