package org.sagebionetworks.dashboard.context;

class EnvReader implements ContextReader {
    @Override
    public String read(String name) {
        return System.getenv(name);
    }
}
