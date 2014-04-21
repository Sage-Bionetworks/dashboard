package org.sagebionetworks.dashboard.http.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

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
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.sagebionetworks.dashboard.context.DashboardContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component("synapseClient")
public class SynapseClient {

    private final Logger logger = LoggerFactory.getLogger(SynapseClient.class);

    @Resource
    private DashboardContext dashboardContext;

    public SynapseClient() {
        PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(20);
        client = new DefaultHttpClient(cm);
    }

    /**
     * @return Session token
     */
    public String login() {
        String usr = dashboardContext.getSynapseUser();
        String pwd = dashboardContext.getSynapsePassword();
        String login = "{\"email\":\"" + usr + "\", \"password\":\"" + pwd + "\"}";
        HttpEntity entity = new StringEntity(login, ContentType.APPLICATION_JSON);
        HttpPost post = new HttpPost(AUTH_LOGIN);
        post.setEntity(entity);
        JsonNode root = executeRequest(post);
        String session = readText(root, "sessionToken");
        if (session == null) {
            throw new RuntimeException("Login failed -- session token is null.");
        }
        return session;
    }

    public String getUserName(final String userId, final String session) {
        String uri = REPO + "/userProfile/" + userId;
        HttpGet get = new HttpGet(uri);
        get.addHeader(new BasicHeader("sessionToken", session));
        JsonNode root = executeRequest(get);
        return readText(root, "userName");
    }

    public String getEntityName(final String entityId, final String session) {
        String uri = REPO + "/entity/" + entityId + "/type";
        HttpGet get = new HttpGet(uri);
        get.addHeader(new BasicHeader("sessionToken", session));
        JsonNode root = executeRequest(get);
        return readText(root, "name");
    }

    public String getBenefactor(final String entityId, final String session) {
        String uri = REPO + "/entity/" + entityId + "/benefactor";
        HttpGet get = new HttpGet(uri);
        get.addHeader(new BasicHeader("sessionToken", session));
        JsonNode root = executeRequest(get);
        return readText(root, "id");
    }

    /**
     * Given an entity, gets the containing project that is directly under the root.
     */
    public String getProject(final String entityId, final String session) {
        String uri = REPO + "/entity/" + entityId + "/ancestors";
        HttpGet get = new HttpGet(uri);
        get.addHeader(new BasicHeader("sessionToken", session));
        JsonNode root = executeRequest(get);
        if (root.has("idList")) { // Skip errors
            int i = 0;
            for (JsonNode ancestor : root.get("idList")) {
                // Reads the second ancestor as the project
                if (i == 1) {
                    return readText(ancestor, "id");
                }
                i++;
            }
            // If there is only one ancestor, it is the root, then this entity itself is a project.
            if (i == 1) {
                return entityId;
            }
        }
        return null;
    }

    public Long getTeamId(final String teamName, final String session) {
        String uri = REPO + "/teams?fragment=" + teamName;
        HttpGet get = new HttpGet(uri);
        get.addHeader(new BasicHeader("sessionToken", session));
        JsonNode root = executeRequest(get);
        for (JsonNode team : root.get("results")) {
            if (teamName.equals(readText(team, "name"))) {
                return team.get("id").asLong();
            }
        }
        return null;
    }

    public boolean isMemberOfTeam(final String userId, final Long teamId, final String session) {
        if (userId == null) {
            return false;
        }
        String uri = REPO + "/team/" + teamId + "/member/" + userId + "/membershipStatus";
        HttpGet get = new HttpGet(uri);
        get.addHeader(new BasicHeader("sessionToken", session));
        JsonNode root = executeRequest(get);
        return root.get("isMember").asBoolean();
    }

    public List<SynapseUser> getUsers(final long offset, final long limit, final String session) {
        List<SynapseUser> users = new ArrayList<SynapseUser>();
        String uri = REPO + "/user?offset=" + offset + "&limit=" + limit;
        HttpGet get = new HttpGet(uri);
        get.addHeader(new BasicHeader("sessionToken", session));
        JsonNode node = executeRequest(get);
        for (JsonNode userNode : node.get("results")) {
            String userId = readText(userNode, "ownerId");
            String userName = readText(userNode, "userName");
            String email = null;
            JsonNode emails = userNode.get("emails");
            if (emails != null && emails.size() > 0) {
                    email = emails.get(0).asText();
            }
            String firstName = readText(userNode, "firstName");
            String lastName = readText(userNode, "lastName");
            SynapseUser user = new SynapseUser(userId, userName, email, firstName, lastName);
            users.add(user);
        }
        return users;
    }

    private JsonNode executeRequest(HttpUriRequest request) {
        return executeRequest(request, 1L, 0);
    }

    private JsonNode executeRequest(final HttpUriRequest request, final long delayInMillis, final int retryCount) {
        if (retryCount > 5) {
            throw new RuntimeException("Failed after 5 retries.");
        }
        InputStream inputStream = null;
        try {
            Thread.sleep(delayInMillis);
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
        } catch (Exception e) {
            int numRetries = retryCount + 1;
            logger.warn("Error executing request. Will retry " + numRetries, e);
            return executeRequest(request, (delayInMillis << 1) + 100L, numRetries);
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

    private String readText(JsonNode jsonNode, String fieldName) {
        JsonNode value = jsonNode.get(fieldName);
        return (value == null ? null : value.asText());
    }

    private static final String AUTH = "https://repo-prod.prod.sagebase.org/auth/v1";
    private static final String AUTH_LOGIN = AUTH + "/session";
    private static final String REPO = "https://repo-prod.prod.sagebase.org/repo/v1";

    private final HttpClient client;
}
