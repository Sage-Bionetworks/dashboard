package org.sagebionetworks.dashboard.service;

import java.util.List;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.NameIdDao;
import org.sagebionetworks.dashboard.dao.SimpleCountDao;
import org.sagebionetworks.dashboard.dao.TimeSeriesDao;
import org.sagebionetworks.dashboard.dao.UniqueCountDao;
import org.sagebionetworks.dashboard.model.Interval;
import org.sagebionetworks.dashboard.model.CountDataPoint;
import org.sagebionetworks.dashboard.model.Statistic;
import org.sagebionetworks.dashboard.model.TimeDataPoint;
import org.springframework.stereotype.Service;

@Service("metricReader")
public class MetricReader {

    @Resource
    private NameIdDao nameIdDao;

    @Resource
    private TimeSeriesDao timeSeriesDao;

    @Resource
    private UniqueCountDao uniqueCountDao;

    @Resource
    private SimpleCountDao simpleCountDao;

    public List<TimeDataPoint> getTimeSeries(String metricName, DateTime from, DateTime to, Statistic s, Interval a) {
        if (metricName == null || metricName.isEmpty()) {
            throw new IllegalArgumentException("Metric name cannot be null or empty.");
        }
        String metricId = getMetricId(metricName);
        return timeSeriesDao.get(metricId, from, to, s, a);
    }

    public List<CountDataPoint> getTop(String metricName, Interval interval, DateTime timestamp, int offset, int size) {
        if (metricName == null || metricName.isEmpty()) {
            throw new IllegalArgumentException("Metric name cannot be null or empty.");
        }
        String metricId = getMetricId(metricName);
        return uniqueCountDao.getTop(metricId, interval, timestamp, offset, size);
    }

    public List<TimeDataPoint> getUniqueCount(String metricName, Interval interval, DateTime from, DateTime to) {
        if (metricName == null || metricName.isEmpty()) {
            throw new IllegalArgumentException("Metric name cannot be null or empty.");
        }
        String metricId = getMetricId(metricName);
        return uniqueCountDao.getUnique(metricId, interval, from, to, 0L, Long.MAX_VALUE);
    }

    public List<TimeDataPoint> getUniqueCount(String metricName, Interval interval, DateTime from, DateTime to, long min, long max) {
        if (metricName == null || metricName.isEmpty()) {
            throw new IllegalArgumentException("Metric name cannot be null or empty.");
        }
        String metricId = getMetricId(metricName);
        return uniqueCountDao.getUnique(metricId, interval, from, to, min, max);
    }

    public List<TimeDataPoint> getCount(String metricName, String id, Interval interval, DateTime from, DateTime to) {
        if (metricName == null || metricName.isEmpty()) {
            throw new IllegalArgumentException("Metric name cannot be null or empty.");
        }
        String metricId = getMetricId(metricName);
        return uniqueCountDao.get(metricId, id, interval, from, to);
    }

    public List<TimeDataPoint> getCount(String metricName, DateTime from, DateTime to) {
        if (metricName == null || metricName.isEmpty()) {
            throw new IllegalArgumentException("Metric name cannot be null or empty.");
        }
        String metricId = getMetricId(metricName);
        return simpleCountDao.get(metricId, from, to);
    }

    private String getMetricId(String metricName) {
        if(!metricName.endsWith("Metric")) {
            metricName = metricName + "Metric";
        }
        if (!nameIdDao.hasName(metricName)) {
            throw new IllegalArgumentException(metricName + " is an invalid metric name or " + 
        "metric does not have any data yet");
        }
        return nameIdDao.getId(metricName);
    }
}
