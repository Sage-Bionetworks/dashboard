package org.sagebionetworks.dashboard.config;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("dashboardConfig")
public class DashboardConfig {

    public DashboardConfig() {
        try {
            String userHome = System.getProperty("user.home");
            File configFile = new File(userHome + "/.dashboard/dashboard.config");
            if (!configFile.exists()) {
                logger.warn("Missing config file " + configFile.getPath());
                // This file is needed as the source of properties
                // which should be overwritten by environment variables
                // or command-line arguments
                configFile = new File(getClass().getResource("/META-INF/dashboard.config").getFile());
            }
            config = new DefaultConfig(configFile.getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getAccessRecordBucket() {
        return config.get("access.record.bucket");
    }

    public String getAwsAccessKey() {
        return config.get("aws.access.key");
    }

    public String getAwsSecretKey() {
        return config.get("aws.secret.key");
    }

    public String getDwUsername() {
        return config.get("dw.username");
    }

    public String getDwPassword() {
        return config.get("dw.password");
    }

    public String getSynapseUser() {
        return config.get("synapse.user");
    }

    public String getSynapsePassword() {
        return config.get("synapse.password");
    }

    public String getGoogleClientId() {
        return config.get("google.client.id");
    }

    public String getGoogleClientSecret() {
        return config.get("google.client.secret");
    }

    public String getUserWhitelist() {
        return config.get("user.whitelist");
    }

    private final Logger logger = LoggerFactory.getLogger(DashboardConfig.class);
    private final Config config;
}
