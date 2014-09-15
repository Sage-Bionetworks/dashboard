package org.sagebionetworks.dashboard.parse;

import java.util.Collection;
import java.util.Collections;

import org.mockito.internal.util.collections.Sets;

/**
 * Filter out records for some specific userId
 */
public class UserIdFilter implements RecordFilter{

    @Override
    public boolean matches(Record record) {
        return !(userIds.contains(record.getUserId()));
    }

    private final Collection<String> userIds =
            Collections.unmodifiableSet(Sets.newSet("3319059" /*dashboard*/));
}
