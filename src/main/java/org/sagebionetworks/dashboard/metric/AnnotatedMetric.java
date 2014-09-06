package org.sagebionetworks.dashboard.metric;

import org.springframework.stereotype.Component;

/**
 * Metric annotated with a name.
 */
abstract class AnnotatedMetric<T> implements Metric<T> {
    @Override
    public String getName() {
        Component c = this.getClass().getAnnotation(Component.class);
        return c.value();
    }
}
