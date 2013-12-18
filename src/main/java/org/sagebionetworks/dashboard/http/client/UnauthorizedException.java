package org.sagebionetworks.dashboard.http.client;

/**
 * Thrown for 401.
 */
public class UnauthorizedException extends RuntimeException {

    private static final long serialVersionUID = 4608028166608126670L;

    public UnauthorizedException() {
        super();
    }
}
