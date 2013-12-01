package org.sagebionetworks.dashboard.service;

import java.io.File;
import java.io.IOException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

class AwsS3Client {

    static final AmazonS3 S3_CLIENT;
    static {
        try {
            // First, look for system properties "aws.accessKeyId" and "aws.secretKey"
            AWSCredentialsProvider p1 = new SystemPropertiesCredentialsProvider();
            // Second, look for configuration file "AwsCredentials.properties" at the current directory
            AWSCredentialsProvider p2 = new ClasspathPropertiesFileCredentialsProvider();
            // Third, look for the gradle configuration file
            String userHome = System.getProperty("user.home");
            File gradleConfig = new File(userHome + "/.gradle/gradle.properties");
            final PropertiesCredentials propCred = new PropertiesCredentials(gradleConfig);
            AWSCredentialsProvider p3 = new AWSCredentialsProvider() {
                @Override
                public AWSCredentials getCredentials() {
                    return propCred;
                }
                @Override
                public void refresh() {}
            };
            // Last, look for environment variables
            AWSCredentialsProvider p4 = new EnvironmentVariableCredentialsProvider();
            AWSCredentialsProviderChain providers = new AWSCredentialsProviderChain(p1, p2, p3, p4);
            S3_CLIENT = new AmazonS3Client(providers);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private AwsS3Client() {}
}
