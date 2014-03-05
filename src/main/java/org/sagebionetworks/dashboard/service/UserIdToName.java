package org.sagebionetworks.dashboard.service;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.dao.SynapseDao;
import org.sagebionetworks.dashboard.model.CountDataPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("userIdToName")
public class UserIdToName implements CountDataPointConverter {

    private Logger logger = LoggerFactory.getLogger(UserIdToName.class);

    @Resource
    private SynapseDao synapseDao;

    @Override
    public CountDataPoint convert(final CountDataPoint in) {
        String id = in.id();
        String name = null;
        try {
            name = synapseDao.getUserName(id);
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
