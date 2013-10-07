package org.sagebionetworks.dashboard.model;

public class RestMethodCall {

    public RestMethodCall(HttpMethod httpMethod, String uri) {
        this.httpMethod = httpMethod;
        this.uri = uri;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return httpMethod + " " + uri;
    }

    private final HttpMethod httpMethod;
    private final String uri;
}
