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
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.zip.GZIPInputStream;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.dao.SessionDedupeDao;
import org.sagebionetworks.dashboard.metric.DayCountMetric;
import org.sagebionetworks.dashboard.metric.ReportMetric;
import org.sagebionetworks.dashboard.metric.SimpleCountMetric;
import org.sagebionetworks.dashboard.metric.TimeSeriesMetric;
import org.sagebionetworks.dashboard.metric.UniqueCountMetric;
import org.sagebionetworks.dashboard.model.WriteRecordResult;
import org.sagebionetworks.dashboard.parse.Record;
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
    private SessionDedupeDao sessionDedupeDao;

    @Resource
    private Collection<SimpleCountMetric> simpleCountMetrics;

    @Resource
    private SimpleCountWriter simpleCountWriter;

    @Resource
    private Collection<TimeSeriesMetric> timeSeriesMetrics;

    @Resource
    private TimeSeriesWriter timeSeriesWriter;

    @Resource
    private Collection<UniqueCountMetric> uniqueCountMetrics;

    @Resource
    private UniqueCountWriter uniqueCountWriter;

    @Resource
    private Collection<DayCountMetric> dayCountMetrics;

    @Resource
    private DayCountWriter dayCountWriter;

    @Resource
    private Collection<ReportMetric> reportMetrics;

    @Resource
    private ReportWriter reportWriter;

    private final RecordParser parser = new RepoRecordParser();

    // Ignore metrics that are not parsed from records
    private final Set<String> ignoreMetrics = Collections.unmodifiableSet(new HashSet<String>(
            Arrays.asList("certifiedUserMetric", "questionPassMetric", "questionFailMetric")));

    private final ExecutorService threadPool = Executors.newFixedThreadPool(100);

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
            List<Record> records = parser.parse(br);
            for (Record record : records) {
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
    private void updateRecord(final Record record, final String file, 
            final int line, final UpdateRecordCallback callback) {
        List<Runnable> tasks = new ArrayList<Runnable>();
        for (final SimpleCountMetric metric : simpleCountMetrics) {
            tasks.add(new Runnable() {
                @Override
                public void run() {
                    try {
                        simpleCountWriter.writeMetric(record, metric);
                    } catch (Throwable e){
                        callback.handle(new WriteRecordResult(false, metric.getName(), file, line));
                    }
                }
            });
        }
        for (final TimeSeriesMetric metric : timeSeriesMetrics) {
            tasks.add(new Runnable() {
                @Override
                public void run() {
                    try {
                        timeSeriesWriter.writeMetric(record, metric);
                    } catch (Throwable e){
                        callback.handle(new WriteRecordResult(false, metric.getName(), file, line));
                    }
                }
            });
        }
        for (final UniqueCountMetric metric: uniqueCountMetrics) {
            if (!ignoreMetrics.contains(metric.getName())) {
                tasks.add(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            uniqueCountWriter.writeMetric(record, metric);
                        } catch (Throwable e){
                            callback.handle(new WriteRecordResult(false, metric.getName(), file, line));
                        }
                    }
                });
            }
        }
        for (final DayCountMetric metric : dayCountMetrics) {
            tasks.add(new Runnable() {
                @Override
                public void run() {
                    try {
                        dayCountWriter.writeMetric(record, metric);
                    } catch (Throwable e){
                        callback.handle(new WriteRecordResult(false, metric.getName(), file, line));
                    }
                }
            });
        }
        for (final ReportMetric metric : reportMetrics) {
            tasks.add(new Runnable() {
                @Override
                public void run() {
                    try {
                        reportWriter.writeMetric(record, metric);
                    } catch (Throwable e){
                        callback.handle(new WriteRecordResult(false, metric.getName(), file, line));
                    }
                }
            });
        }
        for (Runnable task : tasks) {
            threadPool.submit(task);
        }
    }

    private boolean isDone() {
        ThreadPoolExecutor pool = (ThreadPoolExecutor)threadPool;
        return (pool.getActiveCount() == 0 && pool.getQueue().size() == 0);
    }
}
