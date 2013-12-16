package org.sagebionetworks.dashboard.http.client;

/**
 * Thrown for 403.
 */
public class ForbiddenException extends RuntimeException {

    private static final long serialVersionUID = -2268919542857397071L;

    public ForbiddenException() {
        super();
    }
}
