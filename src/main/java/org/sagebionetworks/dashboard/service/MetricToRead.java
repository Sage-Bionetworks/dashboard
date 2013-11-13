package org.sagebionetworks.dashboard.service;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.model.MetricType;
import org.sagebionetworks.dashboard.model.redis.Aggregation;
import org.sagebionetworks.dashboard.model.redis.Statistic;

public class MetricToRead {

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getId() {
        return id;
    }
    public MetricType getType() {
        return type;
    }
    public Aggregation getDefaultAggregation() {
        return defaultAggregation;
    }
    public Statistic getDefaultStatistic() {
        return defaultStatistic;
    }
    public DateTime getDefaultStart() {
        return defaultStart;
    }
    public DateTime getDefaultEnd() {
        return defaultEnd;
    }

    void setName(String name) {
        this.name = name;
    }
    void setDescription(String description) {
        this.description = description;
    }
    void setId(String id) {
        this.id = id;
    }
    void setType(MetricType type) {
        this.type = type;
    }
    void setDefaultAggregation(Aggregation defaultAggregation) {
        this.defaultAggregation = defaultAggregation;
    }
    void setDefaultStatistic(Statistic defaultStatistic) {
        this.defaultStatistic = defaultStatistic;
    }
    void setDefaultStart(DateTime defaultStart) {
        this.defaultStart = defaultStart;
    }
    void setDefaultEnd(DateTime defaultEnd) {
        this.defaultEnd = defaultEnd;
    }

    private String name;
    private String description;
    private String id;
    private MetricType type;
    private Aggregation defaultAggregation;
    private Statistic defaultStatistic;
    private DateTime defaultStart;
    private DateTime defaultEnd;
}
