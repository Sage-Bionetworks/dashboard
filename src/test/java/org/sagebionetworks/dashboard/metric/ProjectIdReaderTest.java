package org.sagebionetworks.dashboard.metric;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.sagebionetworks.dashboard.dao.SynapseDao;
import org.sagebionetworks.dashboard.parse.ProjectIdReader;
import org.sagebionetworks.dashboard.parse.Record;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.springframework.test.util.ReflectionTestUtils;

public class ProjectIdReaderTest {

    @Test
    public void test() {

        final String entityId = "entity ID";
        final String projectId = "project ID";
 
        Record record = mock(Record.class);
        @SuppressWarnings("unchecked")
        RecordReader<String> entityIdReader = (RecordReader<String>)mock(RecordReader.class);
        when(entityIdReader.read(record)).thenReturn(entityId);

        SynapseDao synapseDao = mock(SynapseDao.class);
        when(synapseDao.getProject(entityId)).thenReturn(projectId);

        ProjectIdReader reader = new ProjectIdReader();
        ReflectionTestUtils.setField(reader, "entityIdReader", entityIdReader);
        ReflectionTestUtils.setField(reader, "synapseDao", synapseDao);
        assertEquals(projectId, reader.read(record));
    }
}
