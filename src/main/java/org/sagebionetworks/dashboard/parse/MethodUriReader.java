package org.sagebionetworks.dashboard.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodUriReader implements RecordReader<String> {

    private static final Pattern PATTERN = Pattern.compile("^(/[a-zA-Z0-9/]+/v\\d+/)(.+)");
    private static final Pattern NUMBER = Pattern.compile("\\d+");

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
            final Matcher matcher = PATTERN.matcher(uri);
            if (matcher.matches()) {
                String prefix = matcher.group(1);
                String rest = matcher.group(2);
                rest = NUMBER.matcher(rest).replaceAll("{id}");
                uri = prefix + rest;
            }
        } else {
            uri = "null-uri";
        }

        return method + " " + uri;
    }
}
