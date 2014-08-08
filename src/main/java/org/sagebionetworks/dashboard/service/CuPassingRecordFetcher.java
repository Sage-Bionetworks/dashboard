package org.sagebionetworks.dashboard.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.dao.NameIdDao;
import org.sagebionetworks.dashboard.dao.UniqueCountDao;
import org.sagebionetworks.dashboard.metric.CertifiedUserQuizSubmitMetric;
import org.sagebionetworks.dashboard.metric.CertifiedUserMetric;
import org.sagebionetworks.dashboard.metric.Metric;
import org.springframework.stereotype.Service;

@Service("cuPassingRecordFetcher")
public class CuPassingRecordFetcher {

    @Resource
    private UniqueCountDao uniqueCountDao;

    @Resource
    private NameIdDao nameIdDao;

    @Resource
    private CertifiedUserQuizSubmitMetric submissionMetric;

    @Resource
    private CertifiedUserMetric cuMetric;

    /**
     * @return a list of users who submited a quiz and is not in Certified Users data
     */
    public List<String> getUserIds() {
        List<String> submitUserIds = getUserIds(submissionMetric);
        List<String> certifiedUserIds = getUserIds(cuMetric);
        submitUserIds.removeAll(certifiedUserIds);
        return submitUserIds;
    }

    /*
     * gets a list all userIds from one metric
     */
    private List<String> getUserIds(Metric<String> metric) {
        String metricId = nameIdDao.getId(metric.getName());
        List<String> keys = new ArrayList<String>(uniqueCountDao.getAllKeys(metricId));
        Set<String> userIds = new HashSet<String>();
        for (String key: keys) {
            userIds.addAll(uniqueCountDao.getAllValues(key));
        }
        return new ArrayList<String>(userIds);
    }
}
