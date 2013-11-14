package org.sagebionetworks.dashboard.service;

public interface UpdateCallback {

    public static enum UpdateStatus {
        SUCCEEDED,
        FAILED
    }

    public static class UpdateResult {

        public UpdateResult(
                int fileCount,
                String filePath,
                int lineCount,
                UpdateStatus status) {

            this.fileCount = fileCount;
            this.filePath = filePath;
            this.lineCount = lineCount;
            this.status = status;
        }

        public int getFileCount() {
            return fileCount;
        }

        public String getFilePath() {
            return filePath;
        }

        public int getLineCount() {
            return lineCount;
        }

        public UpdateStatus getStatus() {
            return status;
        }

        @Override
        public String toString() {
            return "UpdateResult [fileCount=" + fileCount + ", filePath="
                    + filePath + ", lineCount=" + lineCount + ", status="
                    + status + "]";
        }

        private final int fileCount;
        private final String filePath;
        private final int lineCount;
        private final UpdateStatus status;
    }

    void call(UpdateResult result);
}
