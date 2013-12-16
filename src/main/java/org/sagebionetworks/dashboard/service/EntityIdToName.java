package org.sagebionetworks.dashboard.service;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.dao.SynapseDao;
import org.sagebionetworks.dashboard.model.CountDataPoint;
import org.springframework.stereotype.Service;

@Service("entityIdToName")
public class EntityIdToName implements CountDataPointConverter {

    @Resource
    private SynapseDao synapseDao;

    @Override
    public CountDataPoint convert(final CountDataPoint in) {
        String id = in.getId();
        String name = synapseDao.getEntityName(id);
        if (name == null) {
            name = id;
        }
        CountDataPoint out = new CountDataPoint(name, in.getCount());
        return out;
    }
}
