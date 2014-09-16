package org.sagebionetworks.dashboard.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads the first matching pattern from a specific field.
 */
public abstract class RegexRecordReader implements RecordReader<String> {

    private final Pattern pattern;

    public RegexRecordReader(Pattern pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("Pattern cannot be null.");
        }
        this.pattern = pattern;
    }

    @Override
    public final String read(AccessRecord record) {
        final String string = readString(record);
        if (string != null && !string.isEmpty()) {
            Matcher matcher = pattern.matcher(string);
            if (matcher.find()) {
                return matcher.group().toLowerCase();
            }
        }
        return null;
    }

    abstract String readString(AccessRecord record);
}
