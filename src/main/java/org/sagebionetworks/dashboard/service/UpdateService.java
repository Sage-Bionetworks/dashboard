package org.sagebionetworks.dashboard.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.annotation.Resource;

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

@Service("updateService")
public class UpdateService {

    private final Logger logger = LoggerFactory.getLogger(UpdateService.class);

    private final RecordParser parser = new RepoRecordParser();

    @Resource
    private MetricRegistry metricRegistry;

    @Resource
    private TimeSeriesWriter timeSeriesWriter;

    @Resource
    private UniqueCountWriter uniqueCountWriter;

    public void update(final InputStream in, final String filePath, final UpdateCallback callback) {

        final Collection<TimeSeriesMetric> tsMetrics = metricRegistry.timeSeriesToWrite();
        final Collection<UniqueCountMetric> ucMetrics = metricRegistry.uniqueCountToWrite();

        int lineCount = 0;
        try {

            GZIPInputStream gzis = new GZIPInputStream(in);
            InputStreamReader ir = new InputStreamReader(gzis, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(ir);
            List<Record> records = parser.parse(br);
            for (Record record : records) {
                for (TimeSeriesMetric metric : tsMetrics) {
                    timeSeriesWriter.writeMetric(record, metric);
                }
                for (UniqueCountMetric metric: ucMetrics) {
                    uniqueCountWriter.writeMetric(record, metric);
                }
                lineCount++;
            }

            UpdateResult result = new UpdateResult(filePath, lineCount, UpdateStatus.SUCCEEDED);
            callback.call(result);
            logger.info(result.toString());

        } catch (Throwable e) {
            UpdateResult result = new UpdateResult(filePath, lineCount, UpdateStatus.FAILED);
            callback.call(result);
            logger.error(result + " with exception " + e);
        }
    }
}
