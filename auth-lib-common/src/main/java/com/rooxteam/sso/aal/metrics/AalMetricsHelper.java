package com.rooxteam.sso.aal.metrics;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;

/**
 * Регистрирует метрики работы с кешем аутентификаций и авторизаций.
 */
public abstract class AalMetricsHelper {
    public static final String METRIC_PRINCIPAL_CACHE_ADD_METER = "principalCacheAddMeter";
    public static final String METRIC_POLICY_CACHE_ADD_METER = "policyCacheAddMeter";
    public static final String METRIC_POLICY_DECISIONS_COUNT_IN_CACHE = "policyDecisionsCountInCache";
    public static final String METRIC_PRINCIPALS_COUNT_IN_CACHE = "principalsCountInCache";

    public static final String METRIC_PRINCIPAL_CACHE_HIT_METER = "principalCacheHitMeter";
    public static final String METRIC_POLICY_CACHE_HIT_METER = "policyCacheHitMeter";

    public static final String METRIC_PRINCIPAL_CACHE_MISS_METER = "principalCacheMissMeter";
    public static final String METRIC_POLICY_CACHE_MISS_METER = "policyCacheMissMeter";

    private final static MeterRegistry metricRegistry;

    static {
        metricRegistry = Metrics.globalRegistry;
    }

    /**
     * Кол-во сохранений principal в кеш, в единицу времени
     */
    private static Counter principalCacheAddMeter = metricRegistry.counter(METRIC_PRINCIPAL_CACHE_ADD_METER);
    /**
     * Кол-во сохранений policy в кеш, в единицу времени
     */
    private static Counter policyCacheAddMeter = metricRegistry.counter(METRIC_POLICY_CACHE_ADD_METER);


    /**
     * Кол-во попаданий в кеш принципалов, в единицу времени
     */
    private static Counter principalCacheHitMeter = metricRegistry.counter(METRIC_PRINCIPAL_CACHE_HIT_METER);
    /**
     * Кол-во попаданий в кеш политик, в единицу времени
     */
    private static Counter policyCacheHitMeter = metricRegistry.counter(METRIC_POLICY_CACHE_HIT_METER);

    /**
     * Кол-во промахов в кеш принципалов, в единицу времени
     */
    private static Counter principalCacheMissMeter = metricRegistry.counter(METRIC_PRINCIPAL_CACHE_MISS_METER);
    /**
     * Кол-во промахов в кеш политик, в единицу времени
     */
    private static Counter policyCacheMissMeter = metricRegistry.counter(METRIC_POLICY_CACHE_MISS_METER);

    public static MeterRegistry getMetricRegistry() {
        return AalMetricsHelper.metricRegistry;
    }

    public static Counter getPrincipalCacheAddMeter() {
        return AalMetricsHelper.principalCacheAddMeter;
    }

    public static Counter getPolicyCacheAddMeter() {
        return AalMetricsHelper.policyCacheAddMeter;
    }

    public static Counter getPrincipalCacheHitMeter() {
        return AalMetricsHelper.principalCacheHitMeter;
    }

    public static Counter getPolicyCacheHitMeter() {
        return AalMetricsHelper.policyCacheHitMeter;
    }

    public static Counter getPrincipalCacheMissMeter() {
        return AalMetricsHelper.principalCacheMissMeter;
    }

    public static Counter getPolicyCacheMissMeter() {
        return AalMetricsHelper.policyCacheMissMeter;
    }
}
