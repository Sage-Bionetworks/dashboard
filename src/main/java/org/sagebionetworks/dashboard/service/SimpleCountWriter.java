package org.sagebionetworks.dashboard.service;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.SimpleCountDao;
import org.springframework.stereotype.Service;

@Service("simpleCountWriter")
public class SimpleCountWriter extends AbstractMetricWriter<String> {

    /**
     * Any value is ignored. Use the empty string "" for the record reader.
     */
    @Override
    void write(String metricId, DateTime timestamp, String value) {
        simpleCountDao.put(metricId, timestamp);
    }

    @Resource
    private SimpleCountDao simpleCountDao;
}
