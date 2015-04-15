package org.sagebionetworks.dashboard.metric;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.sagebionetworks.dashboard.dao.SynapseDao;
import org.sagebionetworks.dashboard.model.AccessRecord;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.springframework.test.util.ReflectionTestUtils;

public class ProjectIdReaderTest {

    @Test
    public void test() {

        final String entityId = "entity ID";
        final String projectId = "project ID";
 
        AccessRecord record = mock(AccessRecord.class);
        @SuppressWarnings("unchecked")
        RecordReader<AccessRecord, String> entityIdReader = (RecordReader<AccessRecord, String>)mock(RecordReader.class);
        when(entityIdReader.read(record)).thenReturn(entityId);

        SynapseDao synapseDao = mock(SynapseDao.class);
        when(synapseDao.getProject(entityId)).thenReturn(projectId);

        ProjectIdReader reader = new ProjectIdReader();
        ReflectionTestUtils.setField(reader, "entityIdReader", entityIdReader);
        ReflectionTestUtils.setField(reader, "synapseDao", synapseDao);
        assertEquals(projectId, reader.read(record));
    }
}
