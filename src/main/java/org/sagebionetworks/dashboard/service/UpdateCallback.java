package org.sagebionetworks.dashboard.service;

import org.sagebionetworks.dashboard.model.WriteRecordResult;

public interface UpdateCallback {

    void call(UpdateResult result);

    public static enum UpdateStatus {
        SUCCEEDED,
        FAILED
    }

    public static class UpdateResult {

        public UpdateResult(
                String filePath,
                int lineCount,
                UpdateStatus status) {

            this.filePath = filePath;
            this.lineCount = lineCount;
            this.status = status;
        }

        public String getFilePath() {
            return filePath;
        }

        /**
         * The number of successfully processed lines.
         */
        public int getLineCount() {
            return lineCount;
        }

        public UpdateStatus getStatus() {
            return status;
        }

        @Override
        public String toString() {
            return "UpdateResult [filePath=" + filePath + ", lineCount="
                    + lineCount + ", status=" + status + "]";
        }

        private final String filePath;
        private final int lineCount;
        private final UpdateStatus status;
    }

    void handle(WriteRecordResult writeRecordResult);
}
