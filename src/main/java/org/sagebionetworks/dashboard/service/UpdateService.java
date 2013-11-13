package org.sagebionetworks.dashboard.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.sagebionetworks.dashboard.parse.Record;
import org.sagebionetworks.dashboard.parse.RecordParser;
import org.sagebionetworks.dashboard.parse.RepoRecordParser;
import org.sagebionetworks.dashboard.service.UpdateCallback.UpdateResult;
import org.sagebionetworks.dashboard.service.UpdateCallback.UpdateStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// TODO: Rewrite into update worker
@Service("updateService")
public class UpdateService {

    @Autowired
    private MetricRegistry metricRegistry;

    @Autowired
    private TimeSeriesWriter timeSeriesWriter;

    @Autowired
    private UniqueCountWriter uniqueCountWriter;

    public void load(File filePath, UpdateCallback callback) {

        Collection<TimeSeriesToWrite> tsMetrics = metricRegistry.timeSeriesToWrite();
        System.out.println("TimeSeriesToWrite: " + tsMetrics.size());
        for (TimeSeriesToWrite tsw : tsMetrics) {
            System.out.println(tsw.getName());
        }
        Collection<UniqueCountToWrite> ucMetrics = metricRegistry.uniqueCountToWrite();
        System.out.println("UniqueCountToWrite: " + ucMetrics.size());
        for (UniqueCountToWrite ucw : ucMetrics) {
            System.out.println(ucw.getName());
        }

        List<File> files = new ArrayList<File>();
        getFiles(filePath, files);
        System.out.println("Total number of files: " + files.size());

        int fileCount = 0;
        File currentFile = null;
        int lineCount = 0;
        RecordParser parser = new RepoRecordParser();
        for (File file : files) {
            try {
                fileCount++;
                currentFile = file;
                FileInputStream fis = new FileInputStream(file);
                GZIPInputStream gis = new GZIPInputStream(fis);
                InputStreamReader isr = new InputStreamReader(gis);
                BufferedReader br = new BufferedReader(isr);
                List<Record> records = parser.parse(br);
                lineCount = 0;
                for (Record record : records) {
                    for (TimeSeriesToWrite metric : tsMetrics) {
                        timeSeriesWriter.writeMetric(record, metric);
                    }
                    for (UniqueCountToWrite metric: ucMetrics) {
                        uniqueCountWriter.writeMetric(record, metric);
                    }
                    lineCount++;
                }
                br.close();
                callback.call(new UpdateResult(fileCount, currentFile.getName(), lineCount,
                        UpdateStatus.SUCCEEDED));
            } catch (IOException e) {
                callback.call(new UpdateResult(fileCount, currentFile.getName(), lineCount,
                        UpdateStatus.FAILED));
            }
        }
    }

    /**
     * Gets all the "csv.gz" files.
     */
    private void getFiles(File file, List<File> files) {
        if (file.isFile()) {
            if (file.getName().endsWith("csv.gz")) {
                files.add(file);
            }
            return;
        }
        File[] moreFiles = file.listFiles();
        for (File f : moreFiles) {
            getFiles(f, files);
        }
    }
}
