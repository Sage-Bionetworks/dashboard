package org.sagebionetworks.dashboard.parse;

import java.util.regex.Pattern;

/**
 * Reads the method and the URI together as one string. For example, "get /repo/v1/version".
 * IDs are represented as "{id}" in the string.
 */
public class MethodUriReader implements RecordReader<String> {

    private static final Pattern ID = Pattern.compile("/[a-z]*(?<!v)(?<!wiki)(?<!wikiheadertree)(?<!md)(\\d+)");
    private static final Pattern MD5 = Pattern.compile("(?<=/md5)/[a-f0-9]+");

    @Override
    public String read(AccessRecord record) {

        // HTTP method
        String method = record.getMethod();
        if (method != null && !method.isEmpty()) {
            method = method.toLowerCase();
        } else {
            method = "null-method";
        }

        // URI
        String uri = record.getUri();
        if (uri != null && !uri.isEmpty()) {
            uri = uri.toLowerCase();
            uri = MD5.matcher(uri).replaceAll("/{md5}");
            uri = ID.matcher(uri).replaceAll("/{id}");
        } else {
            uri = "null-uri";
        }

        return method + " " + uri;
    }
}
