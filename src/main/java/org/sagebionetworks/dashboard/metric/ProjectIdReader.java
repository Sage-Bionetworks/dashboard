package org.sagebionetworks.dashboard.metric;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.dao.SynapseDao;
import org.sagebionetworks.dashboard.parse.EntityIdReader;
import org.sagebionetworks.dashboard.parse.Record;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.springframework.stereotype.Component;

@Component("projectIdReader")
public class ProjectIdReader implements RecordReader<String> {

    @Resource
    private SynapseDao synapseDao;

    private final RecordReader<String> entityIdReader = new EntityIdReader();

    @Override
    public String read(Record record) {
        String entityId = entityIdReader.read(record);
        if (entityId != null) {
            return synapseDao.getProject(entityId);
        }
        return null;
    }
}
