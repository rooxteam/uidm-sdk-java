package com.rooxteam.sso.aal.metrics;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import lombok.Getter;

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

    @Getter
    private final static MetricRegistry metricRegistry;

    static {
        metricRegistry = new MetricRegistry();
        JmxReporter.forRegistry(metricRegistry)
                .inDomain("ru.yota")
                .createsObjectNamesWith(new ObjectNameFactory())
                .build().start();
    }

    /**
     * Кол-во сохранений principal в кеш, в единицу времени
     */
    @Getter
    private static Meter principalCacheAddMeter = metricRegistry.meter(METRIC_PRINCIPAL_CACHE_ADD_METER);
    /**
     * Кол-во сохранений policy в кеш, в единицу времени
     */
    @Getter
    private static Meter policyCacheAddMeter = metricRegistry.meter(METRIC_POLICY_CACHE_ADD_METER);


    /**
     * Кол-во попаданий в кеш принципалов, в единицу времени
     */
    @Getter
    private static Meter principalCacheHitMeter = metricRegistry.meter(METRIC_PRINCIPAL_CACHE_HIT_METER);
    /**
     * Кол-во попаданий в кеш политик, в единицу времени
     */
    @Getter
    private static Meter policyCacheHitMeter = metricRegistry.meter(METRIC_POLICY_CACHE_HIT_METER);

    /**
     * Кол-во промахов в кеш принципалов, в единицу времени
     */
    @Getter
    private static Meter principalCacheMissMeter = metricRegistry.meter(METRIC_PRINCIPAL_CACHE_MISS_METER);
    /**
     * Кол-во промахов в кеш политик, в единицу времени
     */
    @Getter
    private static Meter policyCacheMissMeter = metricRegistry.meter(METRIC_POLICY_CACHE_MISS_METER);

}
