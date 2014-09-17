package org.sagebionetworks.dashboard.parse;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * Filter out records for some specific userId
 */
public class UserIdFilter implements RecordFilter<AccessRecord>{

    @Override
    public boolean matches(AccessRecord record) {
        return !(userIds.contains(record.getUserId()));
    }

    private final Collection<String> userIds = Collections.unmodifiableSet(
            new HashSet<String>(Arrays.asList("3319059" /*dashboard*/)));
}
