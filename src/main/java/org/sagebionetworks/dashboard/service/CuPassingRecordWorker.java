package org.sagebionetworks.dashboard.service;

import java.util.List;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.dao.SynapseDao;
import org.sagebionetworks.dashboard.parse.CuPassingRecord;

public class CuPassingRecordWorker {

    @Resource
    private UpdateCuPassingRecordService updateCertifiedUserService;

    @Resource
    private CuPassingRecordFetcher certifiedUserIdFetcher;

    @Resource
    private SynapseDao synapseDao;

    public void doWork() {
        final List<String> userIds = certifiedUserIdFetcher.getUserIds();
        for (String userId: userIds) {
            CuPassingRecord passingRecord = synapseDao.getCuPassingRecord(userId);
            updateCertifiedUserService.updateCertifiedUsers(passingRecord);
        }
    }
}
