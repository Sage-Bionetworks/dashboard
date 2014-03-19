package org.sagebionetworks.dashboard.context;

class SystemPropertyReader implements ContextReader {
    @Override
    public String read(String name) {
        return System.getProperty(name);
    }
}
