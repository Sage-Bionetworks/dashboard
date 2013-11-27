package org.sagebionetworks.dashboard;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.model.Aggregation;
import org.sagebionetworks.dashboard.model.CountDataPoint;
import org.sagebionetworks.dashboard.model.MetricType;
import org.sagebionetworks.dashboard.model.Statistic;
import org.sagebionetworks.dashboard.model.TimeDataPoint;
import org.sagebionetworks.dashboard.service.MetricQueryService;
import org.sagebionetworks.dashboard.service.MetricRegistry;
import org.sagebionetworks.dashboard.service.MetricToRead;
import org.sagebionetworks.dashboard.service.UpdateCallback;
import org.sagebionetworks.dashboard.service.UpdateService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;

public class App {

    // Run with 'gradle run -PfilePath=/path/to/access/log/files'
    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            throw new IllegalArgumentException("Must provide the file path to the access log files.");
        }
        final File filePath = new File(args[0]);
        if (!filePath.exists()) {
            throw new IllegalArgumentException("File " + filePath.getPath() + " does not exist.");
        }

        final List<File> files = new ArrayList<File>();
        getCsvGzFiles(filePath, files);
        final int total = files.size();
        System.out.println("Total number of files: " + total);

        final ApplicationContext context = new ClassPathXmlApplicationContext("/META-INF/spring/app-context.xml");
        final UpdateService updateService = context.getBean(UpdateService.class);

        // Clear Redis
        StringRedisTemplate redisTemplate = context.getBean(StringRedisTemplate.class);
        Set<String> keys = redisTemplate.keys("*");
        redisTemplate.delete(keys);

        // Load metrics
        int count = 0;
        for (File file : files) {
            count++;
            System.out.println("Loading file " + count + " of " + total);
            InputStream is = null;
            try {
                FileInputStream fis = new FileInputStream(file);
                is = new GZIPInputStream(fis);
                updateService.load(is, file.getPath(), new UpdateCallback() {
                        @Override
                        public void call(UpdateResult result) {
                            System.out.println(result.toString());
                        }
                    });
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

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

    /**
     * Gets all the "csv.gz" files.
     */
    private static void getCsvGzFiles(File file, List<File> files) {
        if (file.isFile()) {
            if (file.getName().endsWith("csv.gz")) {
                files.add(file);
            }
            return;
        }
        File[] moreFiles = file.listFiles();
        for (File f : moreFiles) {
            getCsvGzFiles(f, files);
        }
    }
}
