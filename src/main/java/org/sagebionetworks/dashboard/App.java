package org.sagebionetworks.dashboard;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.model.CountDataPoint;
import org.sagebionetworks.dashboard.model.MetricType;
import org.sagebionetworks.dashboard.model.TimeDataPoint;
import org.sagebionetworks.dashboard.model.redis.Aggregation;
import org.sagebionetworks.dashboard.model.redis.Statistic;
import org.sagebionetworks.dashboard.service.MetricQueryService;
import org.sagebionetworks.dashboard.service.MetricRegistry;
import org.sagebionetworks.dashboard.service.MetricToRead;
import org.sagebionetworks.dashboard.service.UpdateCallback;
import org.sagebionetworks.dashboard.service.UpdateService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;

public class App {

    public static void main(String[] args) throws Exception {

        ApplicationContext context = new ClassPathXmlApplicationContext("/META-INF/spring/app-context.xml");

        // Clear Redis
        StringRedisTemplate redisTemplate = context.getBean(StringRedisTemplate.class);
        Set<String> keys = redisTemplate.keys("*");
        redisTemplate.delete(keys);

        // Write metrics
        UpdateService updateService = context.getBean(UpdateService.class);
        File filePath = new File("/Users/ewu/Documents/repo-records/data");
        updateService.load(filePath, new UpdateCallback() {
            @Override
            public void call(UpdateResult result) {
                System.out.println(result);
            }
        });

        // Query
        MetricQueryService queryService = context.getBean(MetricQueryService.class);
        MetricRegistry registry = context.getBean(MetricRegistry.class);
        List<MetricToRead> metrics = registry.metricsToRead();
        for (MetricToRead metric : metrics) {
            System.out.println(metric.getName());
            System.out.println(metric.getId());
            if (MetricType.TOP == metric.getType()) {
                List<CountDataPoint> topList = queryService.getTop25(
                        metric.getId(), new DateTime(2013, 10, 21, 12, 22));
                for (CountDataPoint d : topList) {
                    System.out.println(d);
                }
                topList = queryService.getTop25(
                        metric.getId(), new DateTime(2013, 10, 23, 12, 22));
                for (CountDataPoint d : topList) {
                    System.out.println(d);
                }
            }
            if (MetricType.UNIQUE_COUNT == metric.getType()) {
                List<TimeDataPoint> uniqueList = queryService.getUniqueCount(metric.getId(),
                        new DateTime(2013, 10, 21, 12, 22),
                        new DateTime(2013, 10, 27, 12, 22));
                for (TimeDataPoint d : uniqueList) {
                    System.out.println(d);
                }
            }
            if (MetricType.TIME_SERIES == metric.getType()) {
                List<TimeDataPoint> tsList = queryService.getTimeSeries(metric.getId(),
                        new DateTime(2013, 10, 21, 12, 22),
                        new DateTime(2013, 10, 22, 12, 22),
                        Statistic.avg,
                        Aggregation.hour);
                for (TimeDataPoint d : tsList) {
                    System.out.println(d);
                }
            }
        }
    }
}
