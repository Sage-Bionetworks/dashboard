package org.sagebionetworks.dashboard.parse;

import java.io.Reader;
import java.util.List;

public interface RecordParser {
    List<AccessRecord> parse(Reader reader);
}
