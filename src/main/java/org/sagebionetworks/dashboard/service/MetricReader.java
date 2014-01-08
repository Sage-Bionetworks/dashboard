package org.sagebionetworks.dashboard.service;

import java.util.List;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.dao.NameIdDao;
import org.sagebionetworks.dashboard.dao.TimeSeriesDao;
import org.sagebionetworks.dashboard.dao.UniqueCountDao;
import org.sagebionetworks.dashboard.model.Aggregation;
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

    public List<TimeDataPoint> getTimeSeries(String metricName, DateTime from, DateTime to,
            Statistic s, Aggregation a) {

        if (metricName == null || metricName.isEmpty()) {
            throw new IllegalArgumentException("Metric name cannot be null or empty.");
        }
        String metricId = getMetricId(metricName);
        return timeSeriesDao.timeSeries(metricId, from, to, s, a);
    }

    public List<CountDataPoint> getTop(String metricName, DateTime timestamp, int n) {

        if (metricName == null || metricName.isEmpty()) {
            throw new IllegalArgumentException("Metric name cannot be null or empty.");
        }
        String metricId = getMetricId(metricName);
        return uniqueCountDao.topCounts(metricId, timestamp, n);
    }

    public List<TimeDataPoint> getUniqueCount(String metricName, DateTime from, DateTime to) {

        if (metricName == null || metricName.isEmpty()) {
            throw new IllegalArgumentException("Metric name cannot be null or empty.");
        }
        String metricId = getMetricId(metricName);
        return uniqueCountDao.uniqueCounts(metricId, from, to);
    }

    private String getMetricId(String metricName) {
        if(!metricName.endsWith("Metric")) {
            metricName = metricName + "Metric";
        }
        if (!nameIdDao.hasName(metricName)) {
            throw new IllegalArgumentException(metricName + " is not a valid metric name.");
        }
        return nameIdDao.getId(metricName);
    }
}
