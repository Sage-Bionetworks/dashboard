package org.sagebionetworks.dashboard.service;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.dao.SynapseDao;
import org.sagebionetworks.dashboard.parse.CuPassingRecord;
import org.springframework.stereotype.Service;

@Service("cuPassingRecordWorker")
public class CuPassingRecordWorker {

    @Resource
    private UpdateCuPassingRecordService updateCertifiedUserService;

    @Resource
    private CuPassingRecordFetcher certifiedUserIdFetcher;

    @Resource
    private SynapseDao synapseDao;

    public void doWork() {
        final Iterable<String> userIds = certifiedUserIdFetcher.getUserIds();
        for (String userId: userIds) {
            CuPassingRecord passingRecord = synapseDao.getCuPassingRecord(userId);
            updateCertifiedUserService.updateCertifiedUsers(passingRecord);
        }
    }
}
