package org.sagebionetworks.dashboard.context;

class EnvReader implements ContextReader {
    @Override
    public String read(String name) {
        name = name.toUpperCase();
        name = name.replace('.', '_');
        return System.getenv(name);
    }
}
