package org.sagebionetworks.dashboard.parse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.dashboard.model.AccessRecord;
import org.sagebionetworks.dashboard.model.SynapseRepoRecord;

public class UriFileUploadFilterTest {
    @Test
    public void test() {
        RecordFilter<AccessRecord> filter = new UriFileUploadFilter();
        SynapseRepoRecord record = new SynapseRepoRecord();
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/fileHandle");
        assertFalse(filter.matches(record));
        record.setUri("/file/v1/fileHandle/179047");
        assertFalse(filter.matches(record));
        record.setUri("/repo/v1/entity/123/file");
        assertFalse(filter.matches(record));
        record.setUri("/file/v1/fileHandle");
        assertTrue(filter.matches(record));
        record.setUri("/file/v1/createChunkedFileUploadToken");
        assertTrue(filter.matches(record));
    }
}
