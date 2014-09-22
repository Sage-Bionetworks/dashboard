package org.sagebionetworks.dashboard.context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("dashboardContext")
public class DashboardContext {

    private Logger logger = LoggerFactory.getLogger(DashboardContext.class);

    private static final String USER_HOME = System.getProperty("user.home");
    private static final String GRADLE_PROPERTIES_FILE = USER_HOME + "/.gradle/gradle.properties";

    // The following two variables are loaded from either the environment or the command line
    private static final String PROD = "prod";

    private final boolean prod;
    private final Map<String, String> nameValueMap;

    public DashboardContext() {
        this(null);
    }

    /**
     * @param properties The input stream of an external properties file.
     */
    public DashboardContext(InputStream properties) {

        prod = Boolean.parseBoolean(readEnvOrProperty(PROD));
        logger.info("Prod: " + prod);

        nameValueMap = new HashMap<String, String>();
        nameValueMap.put("dw.username", "dashboard");
        nameValueMap.put("dw.password", "dashboard");
        nameValueMap.put("aws.access.key", "");
        nameValueMap.put("aws.secret.key", "");
        nameValueMap.put("access.record.bucket", "");
        nameValueMap.put("synapse.user", "");
        nameValueMap.put("synapse.password", "");

        readEnv();
        readGradle();
        readProperties(properties);
        readSystemProperties();
    }

    public boolean isProd() {
        return prod;
    }

    public String getDwUsername() {
        return nameValueMap.get("dw.username");
    }

    public String getDwPassword() {
        return nameValueMap.get("dw.password");
    }

    public String getAwsAccessKey() {
        return nameValueMap.get("aws.access.key");
    }

    public String getAwsSecretKey() {
        return nameValueMap.get("aws.secret.key");
    }

    public String getAccessRecordBucket() {
        return nameValueMap.get("access.record.bucket");
    }

    public String getSynapseUser() {
        return nameValueMap.get("synapse.user");
    }

    public String getSynapsePassword() {
        return nameValueMap.get("synapse.password");
    }

    ////// PRIVATE METHODS //////

    private String readEnvOrProperty(String name) {
        ContextReader envReader = new EnvReader();
        ContextReader sysReader = new SystemPropertyReader();
        String value = envReader.read(name);
        if (value == null) {
            value = sysReader.read(name);
        }
        return value;
    }

    private void readEnv() {
        try {
            populateNameValueMap(new EnvReader());
        } catch (SecurityException x) {
            logger.info("Cannot read environment variables because of SecurityException.");
            return;
        }
    }

    private void readGradle() {
        InputStream inputStream = null;
        try {
            File file = new File(GRADLE_PROPERTIES_FILE);
            inputStream = new FileInputStream(file);
            readProperties(inputStream);
        } catch (FileNotFoundException x) {
            logger.info("Missing Gradle properties file: " + GRADLE_PROPERTIES_FILE);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    private void readProperties(InputStream inputStream) {
        if (inputStream == null) {
            return;
        }
        try {
            Properties properties = new Properties();
            properties.load(inputStream);
            inputStream.close();
            populateNameValueMap(new PropertyReader(properties));
        } catch (IOException x) {
            logger.error("Exception reading " + GRADLE_PROPERTIES_FILE + ": " + x.getMessage());
            return;
        } finally {
            try {
                inputStream.close();
            } catch (IOException x) {
                logger.error(x.getMessage());
            }
        }
    }

    private void readSystemProperties() {
        try {
            populateNameValueMap(new SystemPropertyReader());
        } catch (SecurityException x) {
            logger.info("Cannot read system properties because of SecurityException.");
            return;
        }
    }

    private void populateNameValueMap(ContextReader reader) {
        for (String key : nameValueMap.keySet()) {
            String val = reader.read(key);
            if (prod) {
                String prodKey = key + "." + PROD;
                String prodVal = reader.read(prodKey);
                val = (prodVal == null ? val : prodVal);
            }
            if (val != null) {
                nameValueMap.put(key, val);
            }
        }
    }
}
