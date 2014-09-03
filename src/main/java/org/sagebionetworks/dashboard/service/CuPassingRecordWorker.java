package org.sagebionetworks.dashboard.service;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.dao.SynapseDao;
import org.sagebionetworks.dashboard.parse.CuPassingRecord;
import org.sagebionetworks.dashboard.parse.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("cuPassingRecordWorker")
public class CuPassingRecordWorker {

    private final Logger logger = LoggerFactory.getLogger(CuPassingRecordWorker.class);

    @Resource
    private UpdateCuPassingRecordService updateCertifiedUserService;

    @Resource
    private CuPassingRecordFetcher certifiedUserIdFetcher;

    @Resource
    private SynapseDao synapseDao;

    public void doWork() {
        logger.info("Fetching a list of UserIds from Redis");
        final Iterable<String> userIds = certifiedUserIdFetcher.getUserIds();
        logger.info("Calling Synapse to get PassingRecords for userId: " + userIds);
        for (String userId: userIds) {
            Iterable<Response> responses = synapseDao.getResponses(userId);
            // check to see if the userId really took the test on prod
            if (responses.iterator() == null || !responses.iterator().hasNext()) {
                updateCertifiedUserService.removeSubmission(userId);
                return;
            }
            for (Response response : responses) {
                updateCertifiedUserService.updateResponses(response);
            }
            CuPassingRecord passingRecord = synapseDao.getCuPassingRecord(userId);
            updateCertifiedUserService.updateCertifiedUsers(passingRecord);
            
        }
    }
}
