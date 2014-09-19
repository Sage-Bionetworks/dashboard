package org.sagebionetworks.dashboard.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.zip.GZIPInputStream;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.dao.SessionDedupeDao;
import org.sagebionetworks.dashboard.metric.DayCountMetric;
import org.sagebionetworks.dashboard.metric.Metric;
import org.sagebionetworks.dashboard.metric.SimpleCountMetric;
import org.sagebionetworks.dashboard.metric.TimeSeriesMetric;
import org.sagebionetworks.dashboard.metric.UniqueCountMetric;
import org.sagebionetworks.dashboard.model.WriteRecordResult;
import org.sagebionetworks.dashboard.parse.AccessRecord;
import org.sagebionetworks.dashboard.parse.RecordParser;
import org.sagebionetworks.dashboard.parse.RepoRecordParser;
import org.sagebionetworks.dashboard.service.UpdateFileCallback.UpdateResult;
import org.sagebionetworks.dashboard.service.UpdateFileCallback.UpdateStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("repoUpdateService")
public class RepoUpdateService {

    private final Logger logger = LoggerFactory.getLogger(RepoUpdateService.class);

    @Resource
    private  Collection<SimpleCountMetric> simpleCountMetrics;

    @Resource
    private  Collection<TimeSeriesMetric> timeSeriesMetrics;

    @Resource
    private  Collection<UniqueCountMetric<AccessRecord, String>> uniqueCountMetrics;

    @Resource
    private  Collection<DayCountMetric> dayCountMetrics;

    @Resource
    private SessionDedupeDao sessionDedupeDao;

    private final RecordParser parser = new RepoRecordParser();

    // Ignore metrics that are not parsed from records
    private final Set<String> ignoreMetrics = Collections.unmodifiableSet(new HashSet<String>(
            Arrays.asList("certifiedUserMetric", "questionPassMetric", "questionFailMetric", "topProjectMetric", "topProjectByDayMetric")));

    private final ExecutorService threadPool = Executors.newFixedThreadPool(200);

    public void update(InputStream in, String filePath, 
            UpdateFileCallback fileCallback, UpdateRecordCallback recordCallback) {
        update(in, filePath, 0, fileCallback, recordCallback);
    }

    /**
     * @param in             Input stream to read the metrics.
     * @param filePath       The file path that's behind the input stream.
     *                       This is used as the key to track the progress.
     * @param startLineIncl  1-based starting line number.
     * @param callback       Callback to receive the update results.
     */
    public void update(final InputStream in, final String filePath, final int startLineIncl,
            final UpdateFileCallback fileCallback, final UpdateRecordCallback recordCallback) {
        GZIPInputStream gzis = null;
        InputStreamReader ir = null;
        BufferedReader br = null;
        int lineCount = 0;
        try {
            gzis = new GZIPInputStream(in);
            ir = new InputStreamReader(gzis, StandardCharsets.UTF_8);
            br = new BufferedReader(ir);
            List<AccessRecord> records = parser.parse(br);
            for (AccessRecord record : records) {
                lineCount++;
                if (lineCount >= startLineIncl &&
                        !sessionDedupeDao.isProcessed(record.getSessionId())) {
                    updateRecord(record, filePath, lineCount, recordCallback);
                }
            }
        } catch (Throwable e) {
            //There will be no file with failed status.
            UpdateResult result = new UpdateResult(filePath, lineCount, UpdateStatus.FAILED);
            //fileCallback.call(result);
            logger.error(result.toString(), e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (ir != null) {
                    ir.close();
                }
                if (gzis != null) {
                    gzis.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // Wait for all tasks to finish
        try {
            Thread.sleep(lineCount * 5L);
            while (!isDone()) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        UpdateResult result = new UpdateResult(filePath, lineCount, UpdateStatus.SUCCEEDED);
        fileCallback.call(result);
        logger.info(result.toString());
    }

    public void shutdown() {
        threadPool.shutdown();
    }

    /**
     * Updates a single record.
     */
    private void updateRecord(final AccessRecord record, final String file, 
            final int line, final UpdateRecordCallback callback) {

        List<Runnable> tasks = new ArrayList<Runnable>();
        MetricCollection metricCollection = new MetricCollection();

        for (final Metric<AccessRecord, ?> metric : metricCollection) {
            if (!ignoreMetrics.contains(metric.getName())) {
                tasks.add(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            metric.write(record);
                        } catch (Throwable e){
                            callback.handle(new WriteRecordResult(false, metric.getName(), file, line));
                        }
                    }
                });
            }
        }

        for (Runnable task : tasks) {
            threadPool.submit(task);
        }
    }

    class MetricCollection implements Iterable<Metric<AccessRecord, ?>>{

        private Iterator<SimpleCountMetric> simpleCountMetricPointer;
        private  Iterator<TimeSeriesMetric> timeSeriesMetricPointer;
        private  Iterator<UniqueCountMetric<AccessRecord, String>> uniqueCountMetricPointer;
        private  Iterator<DayCountMetric> dayCountMetricPointer;

        // reset all the pointers
        public MetricCollection() {
            simpleCountMetricPointer = simpleCountMetrics.iterator();
            timeSeriesMetricPointer = timeSeriesMetrics.iterator();
            uniqueCountMetricPointer = uniqueCountMetrics.iterator();
            dayCountMetricPointer = dayCountMetrics.iterator();
        }

        @Override
        public Iterator<Metric<AccessRecord, ?>> iterator() {
            return new MetricCollectionIterator();
        }

        class MetricCollectionIterator implements Iterator<Metric<AccessRecord, ?>> {

            @Override
            public boolean hasNext() {
                return dayCountMetricPointer.hasNext();
            }

            @Override
            public Metric<AccessRecord, ?> next() {
                if (simpleCountMetricPointer.hasNext()) {
                    return simpleCountMetricPointer.next();
                }
                if (timeSeriesMetricPointer.hasNext()) {
                    return timeSeriesMetricPointer.next();
                }
                if (uniqueCountMetricPointer.hasNext()) {
                    return uniqueCountMetricPointer.next();
                }
                return dayCountMetricPointer.next();
            }

            @Override
            public void remove() {
                // do nothing
            }
        }
    }


    private boolean isDone() {
        ThreadPoolExecutor pool = (ThreadPoolExecutor)threadPool;
        return (pool.getActiveCount() == 0 && pool.getQueue().size() == 0);
    }
}
