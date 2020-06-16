package com.rooxteam.sso.aal.metrics;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public final class MicrometerMetricsIntegration implements MetricsIntegration {

    private final static MeterRegistry metricRegistry;

    static {
        metricRegistry = Metrics.globalRegistry;
    }

    /**
     * Кол-во сохранений principal в кеш, в единицу времени
     */
    private static Counter principalCacheAddMeter =
            metricRegistry.counter(MetricNames.METRIC_PRINCIPAL_CACHE_ADD_METER);
    /**
     * Кол-во сохранений policy в кеш, в единицу времени
     */
    private static Counter policyCacheAddMeter = metricRegistry.counter(MetricNames.METRIC_POLICY_CACHE_ADD_METER);


    /**
     * Кол-во попаданий в кеш принципалов, в единицу времени
     */
    private static Counter principalCacheHitMeter =
            metricRegistry.counter(MetricNames.METRIC_PRINCIPAL_CACHE_HIT_METER);
    /**
     * Кол-во попаданий в кеш политик, в единицу времени
     */
    private static Counter policyCacheHitMeter = metricRegistry.counter(MetricNames.METRIC_POLICY_CACHE_HIT_METER);

    /**
     * Кол-во промахов в кеш принципалов, в единицу времени
     */
    private static Counter principalCacheMissMeter =
            metricRegistry.counter(MetricNames.METRIC_PRINCIPAL_CACHE_MISS_METER);
    /**
     * Кол-во промахов в кеш политик, в единицу времени
     */
    private static Counter policyCacheMissMeter = metricRegistry.counter(MetricNames.METRIC_POLICY_CACHE_MISS_METER);

    @Override
    public void registerMapSizeGauge(final String name,
                                     final HashMap<String, String> tags,
                                     final Map mapToMeter) {
        final List<Tag> micrometerTags = new ArrayList<Tag>();
        if (tags != null) {
            for (Map.Entry<String, String> entry : tags.entrySet()) {
                micrometerTags.add(new ImmutableTag(entry.getKey(), entry.getValue()));
            }
        }
        metricRegistry.gaugeMapSize(name, micrometerTags, mapToMeter);
    }

    @Override
    public void incrementPrincipalCacheAddMeter() {
        principalCacheAddMeter.increment();
    }

    @Override
    public void incrementPrincipalCacheHitMeter() {
        principalCacheHitMeter.increment();
    }

    @Override
    public void incrementPrincipalCacheMissMeter() {
        principalCacheMissMeter.increment();
    }

    @Override
    public void incrementPolicyCacheAddMeter() {
        policyCacheAddMeter.increment();
    }

    @Override
    public void incrementPolicyCacheHitMeter() {
        policyCacheHitMeter.increment();
    }

    @Override
    public void incrementPolicyCacheMissMeter() {
        policyCacheMissMeter.increment();
    }
}
