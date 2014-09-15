package org.sagebionetworks.dashboard.parse;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.dao.SynapseDao;
import org.springframework.stereotype.Component;

@Component("benefactorIdReader")
public class BenefactorIdReader implements RecordReader<String> {

    @Resource
    private SynapseDao synapseDao;

    private final RecordReader<String> entityIdReader = new EntityIdReader();

    @Override
    public String read(Record record) {
        String entityId = entityIdReader.read(record);
        if (entityId != null) {
            return synapseDao.getBenefactor(entityId);
        }
        return null;
    }
}
