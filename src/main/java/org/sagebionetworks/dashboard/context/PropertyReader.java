package org.sagebionetworks.dashboard.context;

import java.util.Properties;

class PropertyReader implements ContextReader {

    private final Properties properties;

    PropertyReader(Properties properties) {
        this.properties = properties;
    }

    @Override
    public String read(String name) {
        return properties.getProperty(name);
    }
}
