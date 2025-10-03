package com.rooxteam.sso.aal.metrics;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;

/**
 *
 */
public final class MicrometerMetricsIntegration implements MetricsIntegration {

    private final static MeterRegistry metricRegistry;

    static {
        metricRegistry = Metrics.globalRegistry;
    }

}
