package org.sagebionetworks.dashboard.service;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.dao.SynapseDao;
import org.springframework.stereotype.Service;

@Service("securityService")
public class SecurityService {

    @Resource
    private SynapseDao synapseDao;

    public boolean isDashboardUser(final String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return synapseDao.isDashboardUser(email);
    }
}
