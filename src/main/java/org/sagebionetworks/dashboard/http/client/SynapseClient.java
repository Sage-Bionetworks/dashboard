package org.sagebionetworks.dashboard.http.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SynapseClient {

    public SynapseClient() {
        String user = System.getProperty(SYNAPSE_USR);
        String password = System.getProperty(SYNAPSE_PWD);
        if (user == null || password == null) {
            String userHome = System.getProperty("user.home");
            File gradleConfig = new File(userHome + "/.gradle/gradle.properties");
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(gradleConfig);
                Properties properties = new Properties();
                properties.load(inputStream);
                user = properties.getProperty(SYNAPSE_USR);
                password = properties.getProperty(SYNAPSE_PWD);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        usr = user;
        pwd = password;
        client = new DefaultHttpClient();
    }

    /**
     * @return Session token
     */
    public String login() {

        String login = "{\"email\":\"" + usr + "\", \"password\":\"" + pwd + "\"}";
        HttpEntity entity = new StringEntity(login, ContentType.APPLICATION_JSON);
        HttpPost post = new HttpPost(AUTH_LOGIN);
        post.setEntity(entity);
        JsonNode root = executeRequest(post);
        JsonNode node = root.get("sessionToken");
        String session = node.asText();
        return session;
    }

    public String getDisplayName(final String userId, final String session) {

        String uri = REPO + "/userProfile/" + userId;
        HttpGet get = new HttpGet(uri);
        get.addHeader(new BasicHeader("sessionToken", session));
        JsonNode root = executeRequest(get);
        JsonNode node = root.get("displayName");
        if (node == null) {
            return null;
        }
        return node.asText();
    }

    public String getEntityName(final String entityId, final String session) {

        String uri = REPO + "/entity/" + entityId + "/type";
        HttpGet get = new HttpGet(uri);
        get.addHeader(new BasicHeader("sessionToken", session));
        JsonNode root = executeRequest(get);
        JsonNode node = root.get("name");
        if (node == null) {
            return null;
        }
        return node.asText();
    }

    private JsonNode executeRequest(HttpUriRequest request) {
        InputStream inputStream = null;
        try {
            HttpResponse response = client.execute(request);
            if (HttpStatus.SC_UNAUTHORIZED == response.getStatusLine().getStatusCode()) {
                throw new UnauthorizedException();
            }
            if (HttpStatus.SC_FORBIDDEN == response.getStatusLine().getStatusCode()) {
                throw new ForbiddenException();
            }
            HttpEntity entity = response.getEntity();
            inputStream = entity.getContent();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readValue(inputStream, JsonNode.class);
            return root;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static final String AUTH = "https://repo-prod.prod.sagebase.org/auth/v1";
    private static final String AUTH_LOGIN = AUTH + "/session";
    private static final String REPO = "https://repo-prod.prod.sagebase.org/repo/v1";

    private static final String SYNAPSE_USR = "synapseUsr";
    private static final String SYNAPSE_PWD = "synapsePwd";

    private final HttpClient client;
    private final String usr;
    private final String pwd;
}
