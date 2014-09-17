package org.sagebionetworks.dashboard.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccessRecordUtil {

    /**
     * returns the entityId from a download Uri
     */
    public static String getEntityId(String uri) {
        Pattern pattern = Pattern.compile("/entity/(.*?)/");
        Matcher matcher = pattern.matcher(uri);
        String res = "";
        if (matcher.find()) {
            res = matcher.group(1);
        }
        if (res.startsWith("syn")) {
            res = res.substring(3);
        }
        return res;
    }
}
