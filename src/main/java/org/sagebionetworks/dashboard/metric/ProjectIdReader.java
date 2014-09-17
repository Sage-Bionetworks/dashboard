package org.sagebionetworks.dashboard.metric;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.dao.SynapseDao;
import org.sagebionetworks.dashboard.parse.EntityIdReader;
import org.sagebionetworks.dashboard.parse.AccessRecord;
import org.sagebionetworks.dashboard.parse.RecordReader;
import org.springframework.stereotype.Component;

@Component("projectIdReader")
public class ProjectIdReader implements RecordReader<AccessRecord, String> {

    @Resource
    private SynapseDao synapseDao;

    private final RecordReader<AccessRecord, String> entityIdReader = new EntityIdReader();

    @Override
    public String read(AccessRecord record) {
        String entityId = entityIdReader.read(record);
        if (entityId != null) {
            return synapseDao.getProject(entityId);
        }
        return null;
    }
}
