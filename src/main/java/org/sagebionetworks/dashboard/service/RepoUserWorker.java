package org.sagebionetworks.dashboard.service;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.dao.SynapseDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("repoUserWorker")
public class RepoUserWorker {

    private final Logger logger = LoggerFactory.getLogger(RepoUserWorker.class);

    @Resource
    private SynapseDao synapseDao;

    public void doWork() {
        logger.info("Refreshing the list of Synapse users...");
        synapseDao.refreshUsers();
        logger.info("Done refreshing the list of Synapse users...");
    }
}
