package org.sagebionetworks.dashboard.service;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.dao.SynapseDao;
import org.sagebionetworks.dashboard.model.CountDataPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("entityIdToName")
public class EntityIdToName implements CountDataPointConverter {

    private Logger logger = LoggerFactory.getLogger(EntityIdToName.class);

    @Resource
    private SynapseDao synapseDao;

    @Override
    public CountDataPoint convert(final CountDataPoint in) {
        String id = in.id();
        String name = null;
        try {
            name = synapseDao.getEntityName(id);
        } catch (Exception e) {
            logger.info("Synapse Exception: " + e.getMessage());
            name = null;
        }
        if (name == null) {
            name = id;
        }
        CountDataPoint out = new CountDataPoint(name, in.count());
        return out;
    }
}
