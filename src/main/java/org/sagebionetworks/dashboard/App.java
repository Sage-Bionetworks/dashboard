package org.sagebionetworks.dashboard;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
            InputStream is = new FileInputStream(file);
            try {
                updateService.update(is, file.getPath(), new UpdateCallback() {
                        @Override
                        public void call(UpdateResult result) {
                            System.out.println(result.toString());
                        }
                    });
            } finally {
                if (is != null) {
                    is.close();
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
