package org.sagebionetworks.dashboard.service;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.dao.NameIdDao;
import org.sagebionetworks.dashboard.dao.UniqueCountDao;
import org.sagebionetworks.dashboard.metric.CertifiedUserMetric;
import org.sagebionetworks.dashboard.metric.CertifiedUserQuizSubmitMetric;
import org.sagebionetworks.dashboard.metric.Metric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("cuPassingRecordFetcher")
public class CuPassingRecordFetcher {

    private final Logger logger = LoggerFactory.getLogger(CuPassingRecordFetcher.class);

    @Resource
    private UniqueCountDao uniqueCountDao;

    @Resource
    private NameIdDao nameIdDao;

    @Resource
    private CertifiedUserQuizSubmitMetric submissionMetric;

    @Resource
    private CertifiedUserMetric cuMetric;

    /**
     * @return a list of users who submitted a quiz and is not in Certified Users data
     */
    public Set<String> getUserIds() {
        Set<String> submitUserIds = getUserIds(submissionMetric);
        Set<String> certifiedUserIds = getUserIds(cuMetric);
        submitUserIds.removeAll(certifiedUserIds);
        logger.info(submitUserIds.size() + " userIds are found");
        return submitUserIds;
    }

    /*
     * gets a list all userIds from one metric
     */
    private Set<String> getUserIds(Metric<String> metric) {
        String metricId = nameIdDao.getId(metric.getName());
        Set<String> keys = uniqueCountDao.getAllKeys(metricId);
        Set<String> userIds = new HashSet<String>();
        for (String key : keys) {
            userIds.addAll(uniqueCountDao.getAllValues(key));
        }
        return userIds;
    }
}
