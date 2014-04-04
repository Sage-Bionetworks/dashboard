package org.sagebionetworks.dashboard.http.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

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
import org.apache.http.message.BasicHeader;
import org.sagebionetworks.dashboard.context.DashboardContext;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component("synapseClient")
public class SynapseClient {

    @Resource
    private DashboardContext dashboardContext;

    public SynapseClient() {
        String user = dashboardContext.getSynapseUser();
        String password = dashboardContext.getSynapsePassword();
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

    public String getUserName(final String userId, final String session) {

        String uri = REPO + "/userProfile/" + userId;
        HttpGet get = new HttpGet(uri);
        get.addHeader(new BasicHeader("sessionToken", session));
        JsonNode root = executeRequest(get);
        JsonNode node = root.get("userName");
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

    public Long getTeamId(final String teamName, final String session) {

        String uri = REPO + "/teams?fragment=" + teamName;
        HttpGet get = new HttpGet(uri);
        get.addHeader(new BasicHeader("sessionToken", session));
        JsonNode root = executeRequest(get);
        Iterator<JsonNode> iterator = root.get("results").elements();
        JsonNode team = null;
        while (iterator.hasNext()) {
            team = iterator.next();
            if (teamName.equals(team.get("name").asText())) {
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

    private final HttpClient client;
    private final String usr;
    private final String pwd;
}
