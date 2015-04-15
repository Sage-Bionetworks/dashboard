package org.sagebionetworks.dashboard.config;

import java.io.IOException;

import org.springframework.stereotype.Component;

@Component("dashboardConfig")
public class DashboardConfig {

    public DashboardConfig() {
        try {
            final String srcConfigFile = getClass().getResource("/META-INF/dashboard.config").getFile();
            final String userHome = System.getProperty("user.home");
            final String homeConfigFile = userHome + "/.dashboard/dashboard.config";
            config = new DefaultConfig(srcConfigFile, homeConfigFile);
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

    private final Config config;
}
