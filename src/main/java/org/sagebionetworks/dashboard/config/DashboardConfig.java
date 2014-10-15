package org.sagebionetworks.dashboard.config;

import java.io.IOException;

import org.springframework.stereotype.Component;

@Component("dashboardConfig")
public class DashboardConfig {

    public DashboardConfig() {
        try {
            String userHome = System.getProperty("user.home");
            String configFile = userHome + "/.dashboard/dashboard.config";
            config = new DefaultConfig(configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isProd() {
        return Stack.PROD.equals(config.getStack());
    }

    public String getDwUsername() {
        return config.get("dw.username");
    }

    public String getDwPassword() {
        return config.get("dw.password");
    }

    public String getAwsAccessKey() {
        return config.get("aws.access.key");
    }

    public String getAwsSecretKey() {
        return config.get("aws.secret.key");
    }

    public String getAccessRecordBucket() {
        return config.get("access.record.bucket");
    }

    public String getSynapseUser() {
        return config.get("synapse.user");
    }

    public String getSynapsePassword() {
        return config.get("synapse.password");
    }

    private final Config config;
}
