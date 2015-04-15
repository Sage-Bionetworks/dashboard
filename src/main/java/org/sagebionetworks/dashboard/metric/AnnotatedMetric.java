package org.sagebionetworks.dashboard.metric;

import org.sagebionetworks.dashboard.model.Record;
import org.springframework.stereotype.Component;

/**
 * Metric annotated with a name.
 */
abstract class AnnotatedMetric<R extends Record, V> implements Metric<R, V> {
    @Override
    public String getName() {
        Component c = this.getClass().getAnnotation(Component.class);
        return c.value();
    }
}
