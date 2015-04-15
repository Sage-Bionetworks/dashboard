package org.sagebionetworks.dashboard.parse;

import java.io.Reader;
import java.util.List;

import org.sagebionetworks.dashboard.model.AccessRecord;

public interface RecordParser {
    List<AccessRecord> parse(Reader reader);
}
