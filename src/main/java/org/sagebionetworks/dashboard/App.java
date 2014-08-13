package org.sagebionetworks.dashboard;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.sagebionetworks.dashboard.service.CuPassingRecordWorker;
import org.sagebionetworks.dashboard.service.RepoUpdateService;
import org.sagebionetworks.dashboard.service.RepoUserWorker;
import org.sagebionetworks.dashboard.service.UpdateCallback;
import org.slf4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
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

        final ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("/META-INF/spring/app-context.xml");
        final RepoUserWorker userWorker = context.getBean(RepoUserWorker.class);
        final Logger logger = org.slf4j.LoggerFactory.getLogger(App.class);
        logger.info("Loading Synapse users.");
        userWorker.doWork();
        logger.info("Done loading Synapse users.");

        final List<File> files = new ArrayList<File>();
        getCsvGzFiles(filePath, files);
        final int total = files.size();
        logger.info("Total number of files: " + total);

        context.registerShutdownHook();
        final RepoUpdateService updateService = context.getBean(RepoUpdateService.class);
        final CuPassingRecordWorker passingRecordWorker = context.getBean(CuPassingRecordWorker.class);

        // Clear Redis
        StringRedisTemplate redisTemplate = context.getBean(StringRedisTemplate.class);
        Set<String> keys = redisTemplate.keys("*");
        redisTemplate.delete(keys);

        // Load metrics
        for (int i = files.size() - 1; i >= 0; i--) {
            File file = files.get(i);
            logger.info("Loading file " + (files.size() - i) + " of " + total);
            InputStream is = new FileInputStream(file);
            try {
                updateService.update(is, file.getPath(), new UpdateCallback() {
                        @Override
                        public void call(UpdateResult result) {
                            logger.info(result.toString());
                        }
                    });
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }
        passingRecordWorker.doWork();

        // Close the context when done
        context.close();
    }

    /**
     * Gets all the "csv.gz" files but excluse the "rolling" ones.
     */
    private static void getCsvGzFiles(File file, List<File> files) {
        if (file.isFile()) {
            final String fileName = file.getName();
            if (fileName.endsWith("csv.gz") && !fileName.contains("rolling")) {
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
