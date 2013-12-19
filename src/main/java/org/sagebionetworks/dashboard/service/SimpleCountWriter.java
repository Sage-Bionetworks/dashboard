package org.sagebionetworks.dashboard.service;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.CountDao;
import org.springframework.stereotype.Service;

@Service("countWriter")
public class SimpleCountWriter extends AbstractMetricWriter<String> {

    /**
     * Any value is ignored. Use the empty string "" for the record reader.
     */
    @Override
    void write(String metricId, DateTime timestamp, String value) {
        countDao.put(metricId, timestamp);
    }

    @Resource
    private CountDao countDao;
}
