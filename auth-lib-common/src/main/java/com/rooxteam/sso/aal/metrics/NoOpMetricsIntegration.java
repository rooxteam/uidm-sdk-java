package com.rooxteam.sso.aal.metrics;

import java.util.HashMap;
import java.util.Map;

public class NoOpMetricsIntegration implements MetricsIntegration {
    @Override
    public void registerMapSizeGauge(final String metricPolicyDecisionsCountInCache,
                                     final HashMap<String, String> stringStringHashMap,
                                     final Map policyDecisionKeyEvaluationResponseConcurrentMap) {
    }

    @Override
    public void incrementPrincipalCacheAddMeter() {

    }

    @Override
    public void incrementPrincipalCacheHitMeter() {

    }

    @Override
    public void incrementPrincipalCacheMissMeter() {

    }

    @Override
    public void incrementPolicyCacheAddMeter() {

    }

    @Override
    public void incrementPolicyCacheHitMeter() {

    }

    @Override
    public void incrementPolicyCacheMissMeter() {

    }
}
