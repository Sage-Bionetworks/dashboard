package org.sagebionetworks.dashboard.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.metric.SimpleCountMetric;
import org.sagebionetworks.dashboard.metric.TimeSeriesMetric;
import org.sagebionetworks.dashboard.metric.UniqueCountMetric;
import org.sagebionetworks.dashboard.parse.Record;
import org.sagebionetworks.dashboard.parse.RecordParser;
import org.sagebionetworks.dashboard.parse.RepoRecordParser;
import org.sagebionetworks.dashboard.service.UpdateCallback.UpdateResult;
import org.sagebionetworks.dashboard.service.UpdateCallback.UpdateStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("repoUpdateService")
public class RepoUpdateService {

    private final Logger logger = LoggerFactory.getLogger(RepoUpdateService.class);

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

    private final RecordParser parser = new RepoRecordParser();

    public void update(final InputStream in, final String filePath, final UpdateCallback callback) {

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
                updateRecord(record);
                lineCount++;
            }
            UpdateResult result = new UpdateResult(filePath, lineCount, UpdateStatus.SUCCEEDED);
            callback.call(result);
            logger.info(result.toString());
        } catch (Throwable e) {
            UpdateResult result = new UpdateResult(filePath, lineCount, UpdateStatus.FAILED);
            callback.call(result);
            logger.error(result + " with exception " + e);
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
    }

    /**
     * Updates a single record.
     */
    public void updateRecord(Record record) {
        for (SimpleCountMetric metric : simpleCountMetrics) {
            simpleCountWriter.writeMetric(record, metric);
        }
        for (TimeSeriesMetric metric : timeSeriesMetrics) {
            timeSeriesWriter.writeMetric(record, metric);
        }
        for (UniqueCountMetric metric: uniqueCountMetrics) {
            uniqueCountWriter.writeMetric(record, metric);
        }
    }
}
