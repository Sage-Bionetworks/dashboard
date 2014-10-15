package org.sagebionetworks.dashboard.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.sagebionetworks.dashboard.config.DashboardConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;

@Service("repoFolderFetcher")
public class RepoFolderFetcher {

    private Logger logger = LoggerFactory.getLogger(RepoFolderFetcher.class);

    // Increase this to skip really old stacks
    private static final String STACK_START = "000000010";
    // Create test stacks outside this cutoff point
    private static final String STACK_END   = "000001000";

    @Resource
    private DashboardConfig dashboardConfig;

    @Resource
    private AmazonS3 s3Client;

    /**
     * Gets the most recent folders as S3 prefixes ordered reversely by date.
     *
     * @param days How many days to fetch at most
     */
    public List<String> getRecentFolders(final int days) {
        return getRecentFolders(days, STACK_START, STACK_END);
    }

    /**
     * Gets the most recent folders as S3 prefixes ordered reversely by date.
     *
     * @param days How many days to fetch at most
     */
    public List<String> getRecentFolders(final int days, final String stackStart, final String stackEnd) {
        if (days <= 0) {
            throw new IllegalArgumentException("The number of days must be > 0.");
        }
        // Get the list of stacks
        final String bucket = dashboardConfig.getAccessRecordBucket();
        ListObjectsRequest listStacks = new ListObjectsRequest();
        listStacks.setBucketName(bucket);
        // Using a delimiter switches the listObjects() call to
        // retrieve a list of common prefixes instead and
        // the list of object summaries will be empty.
        listStacks.setDelimiter("/");
        listStacks.setMarker(stackStart);
        ObjectListing stackListing = s3Client.listObjects(listStacks);
        List<String> stackList = new ArrayList<String>();
        getStacks(stackList, stackListing, stackEnd);
        // For each stack, get the list of dates
        List<Folder> folderList = new ArrayList<Folder>();
        for (String stack : stackList) {
            ListObjectsRequest listDates = new ListObjectsRequest();
            listDates.withBucketName(bucket);
            listDates.withPrefix(stack);
            listDates.withDelimiter("/");
            ObjectListing dateListing = s3Client.listObjects(listDates);
            List<String> dates = dateListing.getCommonPrefixes();
            for (String date : dates) {
                date = date.replace(stack, "");
                Folder folder = new Folder(stack, date);
                folderList.add(folder);
            }
        }
        // Sort folder list
        Collections.sort(folderList);
        // Cut off the list at the max number of days
        List<String> prefixList = new ArrayList<String>();
        Set<String> dateSet = new HashSet<String>();
        for (Folder folder : folderList) {
            dateSet.add(folder.date);
            if (dateSet.size() > days) {
                return prefixList;
            }
            prefixList.add(folder.toString());
        }
        logger.info("A list of " + prefixList.size() + " folders, spanning at maximum "
                + days + " days, have been retrieved.");
        return prefixList;
    }

    /**
     * Gets the stacks as S3 prefixes in alphabetical order.
     * Note these are the stack number strings with a trailing '/'.
     */
    private void getStacks(final List<String> stackList, ObjectListing objListing,
            final String stackEnd) {
        List<String> stacks = objListing.getCommonPrefixes();
        for (String stack : stacks) {
            if (stack.compareTo(stackEnd) > 0) {
                // We are already beyond the limit
                return;
            }
            stackList.add(stack);
        }
        if (objListing.isTruncated()) {
            getStacks(stackList, s3Client.listNextBatchOfObjects(objListing), stackEnd);
        }
    }

    /**
     * This basically is the path between the bucket and the file.
     * It combines a stack, like "000000022", with a date, example "2013-11-30".
     * Sorting is done reversely on the date first then on the stack
     * so that the most recent data get processed first.
     * <p>
     * This is also the unit, within which we maintain a marker to track
     * the progress (See RepoFileFetcher).
     */
    private static class Folder implements Comparable<Folder>{
        private final Pattern STACK_REGEX = Pattern.compile("(" +RepoFolderFetcher.class.getSimpleName() + "Test)?[0-9]+/");
        private final Pattern DATE_REGEX = Pattern.compile("[0-9]{4}-[0-1][0-9]-[0-3][0-9]/");
        private final String stack;
        private final String date;
        private Folder(String stack, String date) {
            if (!STACK_REGEX.matcher(stack).matches()) {
                throw new IllegalArgumentException("Stack " + stack + " is of invalid format.");
            }
            if (!DATE_REGEX.matcher(date).matches()) {
                throw new IllegalArgumentException("Date " + date + " is of invalid format.");
            }
            this.stack = stack;
            this.date = date;
        }
        @Override
        public int compareTo(Folder that) {
            // Compare first date then stack
            // Sort reversely
            int score = that.date.compareTo(this.date);
            if (score == 0) {
                return that.stack.compareTo(this.stack);
            }
            return score;
        }
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((date == null) ? 0 : date.hashCode());
            result = prime * result + ((stack == null) ? 0 : stack.hashCode());
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Folder other = (Folder) obj;
            if (date == null) {
                if (other.date != null)
                    return false;
            } else if (!date.equals(other.date)) {
                return false;
            }
            if (stack == null) {
                if (other.stack != null)
                    return false;
            } else if (!stack.equals(other.stack)) {
                return false;
            }
            return true;
        }
        @Override
        public String toString() {
            return stack + date;
        }
    }
}
