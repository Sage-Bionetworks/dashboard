package org.sagebionetworks.dashboard.http.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.dashboard.context.DashboardContext;
import org.sagebionetworks.dashboard.parse.CuPassingRecord;
import org.sagebionetworks.dashboard.parse.CuResponseRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

@Component("synapseClient")
public class SynapseClient {

    private final Logger logger = LoggerFactory.getLogger(SynapseClient.class);

    @Resource
    private DashboardContext dashboardContext;

    public SynapseClient() {
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(200);
        connManager.setDefaultMaxPerRoute(20);
        HttpRequestRetryHandler retryHandler = new DefaultHttpRequestRetryHandler();
        client = HttpClientBuilder
                .create()
                .setConnectionManager(connManager)
                .setRetryHandler(retryHandler)
                .build();
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
        logger.warn("Got null project for entity " + entityId + ". " + root.toString());
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
        if (node.get("results") == null) {
            return users;
        }
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

    public CuPassingRecord getCuPassingRecord(final String userId, final String session) {
        if (userId == null) {
            return null;
        }
        String uri = REPO + "/user/" + userId + "/certifiedUserPassingRecord";
        HttpGet get = new HttpGet(uri);
        get.addHeader(new BasicHeader("sessionToken", session));
        JsonNode root = executeRequest(get);

        if (root == null || readText(root, "reason") != null) {
            return null;
        }
        boolean isPassed = root.get("passed").booleanValue();
        DateTime timestamp = ISODateTimeFormat.dateTime().parseDateTime(readText(root, "passedOn"));
        int score = root.get("score").intValue();

        return new CuPassingRecord(isPassed, userId, timestamp, score);
    }

    public List<CuResponseRecord> getResponses(String userId, String session) {
        if (userId == null) {
            return null;
        }
        String uri = REPO + "/user/" + userId + "/certifiedUserPassingRecords";
        uri += "?limit=" + Integer.toString(Integer.MAX_VALUE) + "&offset=0";
        HttpGet get = new HttpGet(uri);
        get.addHeader(new BasicHeader("sessionToken", session));
        JsonNode root = executeRequest(get);

        List<CuResponseRecord> res = new ArrayList<CuResponseRecord>();
        if (root == null || root.get("totalNumberOfResults").intValue() == 0) {
            return res;
        }

        Iterator<JsonNode> it = root.get("results").iterator();
        while (it.hasNext()) {
            JsonNode passingRecord = it.next();
            int respId = passingRecord.get("responseId").intValue();
            DateTime timestamp = ISODateTimeFormat.dateTime().parseDateTime(readText(passingRecord, "passedOn"));
            Iterator<JsonNode> responses = passingRecord.get("corrections").iterator();
            while (responses.hasNext()) {
                JsonNode response = responses.next();
                boolean isCorrect = response.get("isCorrect").booleanValue();
                int questionIndex = response.get("question").get("questionIndex").intValue();
                CuResponseRecord resp = new CuResponseRecord(respId, questionIndex, timestamp, isCorrect);
                res.add(resp);
            }
        }

        return res;
    }

    private JsonNode executeRequest(HttpUriRequest request) {
        try {
            return executeRequestWithRetry(request);
        } catch (Throwable e) {
            logger.error("Request failed. Returning null JSON node.", e);
            return JsonNodeFactory.instance.nullNode();
        }
    }

    private JsonNode executeRequestWithRetry(final HttpUriRequest request) {
        final int maxNumRetries = 7;
        int retryCount = 0;
        while (retryCount < maxNumRetries) {
            try {
                final HttpResponse response = client.execute(request);
                final int status = response.getStatusLine().getStatusCode();
                if (status / 100 == 5 || status == 429) {
                    logger.warn("Error executing request. "
                            + readResponse(response).toString()
                            + " Will do retry #" + retryCount + ".");
                    retryCount++;
                    Thread.sleep(2 ^ retryCount * 100);
                    continue;
                }
                if (HttpStatus.SC_UNAUTHORIZED == status) {
                    throw new UnauthorizedException();
                }
                if (HttpStatus.SC_FORBIDDEN == status) {
                    throw new ForbiddenException();
                }
                return readResponse(response);
            } catch (ClientProtocolException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("Failed after " + retryCount +" retries.");
    }

    private JsonNode readResponse(HttpResponse response) {
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        try {
            final HttpEntity entity = response.getEntity();
            // Get char set
            final String contentType = entity.getContentType().getValue();
            int begin = contentType.indexOf("charset=") + "charset=".length();
            int end = contentType.indexOf(";", begin);
            end = end < 0 ? contentType.length() : end;
            String encodingType = contentType.substring(begin, end);
            // Get as JSON node
            inputStream = entity.getContent();
            inputStreamReader = new InputStreamReader(inputStream, encodingType);
            JsonNode root = (new ObjectMapper()).readValue(inputStreamReader, JsonNode.class);
            return root;
        } catch (IllegalStateException e) {
            throw new RuntimeException(e);
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
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
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
