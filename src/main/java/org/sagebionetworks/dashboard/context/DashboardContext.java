package org.sagebionetworks.dashboard.context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// TODO:
// 1) Consolidate ServiceContext, SynapseClient here
// 2) Add encryption
@Component("dashboardContext")
public class DashboardContext {

    private Logger logger = LoggerFactory.getLogger(DashboardContext.class);

    private static final String USER_HOME = System.getProperty("user.home");
    private static final String GRADLE_PROPERTIES_FILE = USER_HOME + "/.gradle/gradle.properties";
    private static final String PROD = "prod";

    private final boolean prod;
    private final Map<String, String> nameValueMap;

    public DashboardContext() {
        this(null);
    }

    /**
     * @param properties Can be read from a properties file
     */
    public DashboardContext(Properties properties) {

        prod = Boolean.parseBoolean(System.getProperty(PROD));

        nameValueMap = new HashMap<String, String>();
        nameValueMap.put("dw.username", "username");
        nameValueMap.put("dw.password", "password");

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

    ////// PRIVATE METHODS //////

    private void readEnv() {
        try {
            populateNameValueMap(new EnvReader());
        } catch (SecurityException x) {
            logger.info("Cannot read environment variables because of SecurityException.");
            return;
        }
    }

    private void readGradle() {
        FileInputStream inStream = null;
        try {
            File file = new File(GRADLE_PROPERTIES_FILE);
            inStream = new FileInputStream(file);
            Properties properties = new Properties();
            properties.load(inStream);
            inStream.close();
            populateNameValueMap(new PropertyReader(properties));
        } catch (FileNotFoundException x) {
            logger.info("Missing Gradle properties file: " + GRADLE_PROPERTIES_FILE);
        } catch (IOException x) {
            logger.error("Exception reading " + GRADLE_PROPERTIES_FILE + ": " + x.getMessage());
            return;
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException x) {
                logger.error(x.getMessage());
            }
        }
    }

    private void readProperties(Properties properties) {
        if (properties != null) {
            populateNameValueMap(new PropertyReader(properties));
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
