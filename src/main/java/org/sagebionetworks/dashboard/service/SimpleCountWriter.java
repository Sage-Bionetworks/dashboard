package org.sagebionetworks.dashboard.service;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.SimpleCountDao;
import org.sagebionetworks.dashboard.parse.AccessRecord;
import org.springframework.stereotype.Service;

@Service("simpleCountWriter")
public class SimpleCountWriter extends AbstractMetricWriter<AccessRecord, String> {

    @Resource
    private SimpleCountDao simpleCountDao;

    @Override
    void write(String metricId, String additionalKey, DateTime timestamp,
            String id) {
        simpleCountDao.put(metricId + additionalKey, timestamp);
    }
}
