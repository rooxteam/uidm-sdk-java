package com.rooxteam.sso.aal.metrics;

import java.util.HashMap;
import java.util.Map;

/**
 * Регистрирует и инкрементит метрики работы с кешем аутентификаций и авторизаций.
 */
public interface MetricsIntegration {


    void registerMapSizeGauge(final String metricPolicyDecisionsCountInCache,
                              final HashMap<String, String> stringStringHashMap,
                              final Map policyDecisionKeyEvaluationResponseConcurrentMap);

    void incrementPrincipalCacheAddMeter();

    void incrementPrincipalCacheHitMeter();

    void incrementPrincipalCacheMissMeter();

    void incrementPolicyCacheAddMeter();

    void incrementPolicyCacheHitMeter();

    void incrementPolicyCacheMissMeter();
}
