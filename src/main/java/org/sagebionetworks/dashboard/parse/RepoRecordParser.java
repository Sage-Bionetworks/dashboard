package org.sagebionetworks.dashboard.parse;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;

public class RepoRecordParser implements RecordParser {

    public RepoRecordParser() {

        String[] headers = new String[] {
                "returnObjectId",
                "elapseMS",
                "timestamp",
                "via",
                "host",
                "threadId",
                "userAgent",
                "queryString",
                "sessionId",
                "xForwardedFor",
                "requestURL",
                "userId",
                "origin",
                "date",
                "method",
                "vmId",
                "instance",
                "stack",
                "success"};

        Map<String, Integer> mappings = new HashMap<String, Integer>();
        for (int i = 0; i < headers.length; i++) {
            mappings.put(headers[i], Integer.valueOf(i));
        }
        headerMappings = Collections.unmodifiableMap(mappings);
    }

    @Override
    public List<Record> parse(Reader r) {
        List<Record> records = new ArrayList<Record>();
        CSVReader reader = null;
        try {
            reader = new CSVReader(r);
            String [] nextLine = reader.readNext();
            while (nextLine != null) {
                records.add(parse(nextLine));
                nextLine = reader.readNext();
            }
            return records;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private RepoRecord parse(String[] line) {
        RepoRecord record = new RepoRecord();
        record.setSessionId(getString("sessionId", line));
        record.setTimestamp(getLong("timestamp", line));
        record.setUserId(getString("userId", line));
        record.setObjectId(getString("returnObjectId", line));
        record.setMethod(getString("method", line));
        record.setUri(getString("requestURL", line));
        record.setQueryString(getString("queryString", line));
        record.setStatus(getString("success", line));
        record.setLatency(getLong("elapseMS", line));
        record.setUserAgent(getString("userAgent", line));
        record.setStack(getString("stack", line));
        record.setHost(getString("host", line));
        return record;
    }

    private String getString(String header, String[] line) {
        int i = headerMappings.get(header).intValue();
        String str = line[i];
        if (str != null && !str.isEmpty()) {
            return str;
        }
        return null; // This resets empty string to null
    }

    private Long getLong(String header, String[] line) {
        String str = getString(header, line);
        if (str != null) {
            return Long.valueOf(Long.parseLong(str));
        }
        return null;
    }

    private final Map<String, Integer> headerMappings;
}
