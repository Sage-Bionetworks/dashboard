package org.sagebionetworks.dashboard.parse;

import java.util.Arrays;
import java.util.List;

/**
 * Filter out records for some specific userId
 */
public class UserIdFilter implements RecordFilter{

    @Override
    public boolean matches(Record record) {
        return !userIds.contains(record);
    }

    private final List<String> userIds = Arrays.asList("3319059" /*dashboard*/);
}
