package org.sagebionetworks.dashboard.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads the method and the URI together as one string. For example, "get /repo/v1/version".
 * IDs are represented as "{id}" in the string.
 */
public class MethodUriReader implements RecordReader<String> {

    private static final Pattern ID = Pattern.compile("/[a-z]*(?<!v)(?<!wiki)(?<!wikiheadertree)(\\d+)");

    @Override
    public String read(Record record) {

        // Get the HTTP method
        String method = record.getMethod();
        if (method != null && !method.isEmpty()) {
            method = method.toLowerCase();
        } else {
            method = "null-method";
        }

        // Attach the URI if it is not null
        String uri = record.getUri();
        if (uri != null && !uri.isEmpty()) {
            uri = uri.toLowerCase();
            final Matcher matcher = ID.matcher(uri);
            StringBuilder uriBuilder = new StringBuilder();
            int start = 0;
            while (matcher.find()) {
                // Append the non-matched part
                int end = matcher.start();
                uriBuilder.append(uri.substring(start, end));
                start = matcher.end();
                // Inspect the matched part to replace IDs
                String matched = matcher.group();
                matched = matched.replaceAll("\\d+", "{id}");
                uriBuilder.append(matched);
            }
            uriBuilder.append(uri.substring(start, uri.length()));
            uri = uriBuilder.toString();
        } else {
            uri = "null-uri";
        }

        return method + " " + uri;
    }
}
