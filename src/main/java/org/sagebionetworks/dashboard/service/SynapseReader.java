package org.sagebionetworks.dashboard.service;

import java.util.List;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.dao.SynapseDao;
import org.springframework.stereotype.Service;

@Service("synapseReader")
public class SynapseReader {

    @Resource
    private SynapseDao synapseDao;

    List<String> getUserNames(List<String> userIds) {
        return synapseDao.getUserNames(userIds);
    }

    List<String> getEntityNames(List<String> entityIds) {
        return synapseDao.getEntityNames(entityIds);
    }

    List<String> getProjects(List<String> entityIds) {
        return synapseDao.getProjects(entityIds);
    }
}
