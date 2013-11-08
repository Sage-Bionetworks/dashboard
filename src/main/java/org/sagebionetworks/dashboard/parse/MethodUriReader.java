package org.sagebionetworks.dashboard.parse;

import java.util.regex.Pattern;

public class MethodUriReader implements RecordReader<String> {

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
            Pattern number = Pattern.compile("\\d+");
            uri = number.matcher(uri).replaceAll("[num]");
            uri = uri.toLowerCase();
        } else {
            uri = "null-uri";
        }

        return method + " " + uri;
    }
}
