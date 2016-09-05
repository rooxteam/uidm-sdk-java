package com.rooxteam.sso.aal;

import com.google.common.cache.Cache;
import com.rooxteam.sso.aal.client.SsoTokenClient;
import com.rooxteam.sso.aal.client.TokenHelper;
import com.rooxteam.sso.aal.client.model.EvaluationResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.*;

/**
 * Фоновая задача по удалению из кеша неактивных принципалов и авторизационных решений для них.
 */
@RequiredArgsConstructor
public class PollingBean extends TimerTask {

    @NonNull
    private SsoTokenClient ssoTokenClient;
    @NonNull
    private Cache<PolicyDecisionKey, EvaluationResponse> isAllowedPolicyDecisionsCache;
    @NonNull
    private Cache<PrincipalKey, Principal> principalCache;
    @NonNull
    private List<PrincipalEventListener> principalEventListeners;

    @Override
    public void run() {
        Map<Principal, List<AalCacheKey>> cachedPrincipals = getCachedPrincipals();

        for (Map.Entry<Principal, List<AalCacheKey>> entry : cachedPrincipals.entrySet()) {
            Principal cachedPrincipal = entry.getKey();
            String tokenId = TokenHelper.getId(cachedPrincipal.getJwtToken());
            if (!ssoTokenClient.queryExistence(tokenId)) {
                List<AalCacheKey> cacheKeys = entry.getValue();
                for (AalCacheKey cacheKey : cacheKeys) {
                    if (cacheKey instanceof PrincipalKey) {
                        principalCache.invalidate(cacheKey);
                    } else if (cacheKey instanceof PolicyDecisionKey) {
                        isAllowedPolicyDecisionsCache.invalidate(cacheKey);
                    }
                }
                fireOnInvalidate(cachedPrincipal);
            }
        }

        AalLogger.LOG.traceCacheInvalidatingByPolling();
    }

    private Map<Principal, List<AalCacheKey>> getCachedPrincipals() {
        Map<Principal, List<AalCacheKey>> cachedPrincipals = new HashMap<>();
        for (Map.Entry<PrincipalKey, Principal> entry : principalCache.asMap().entrySet()) {
            addCacheKey(cachedPrincipals, entry.getValue(), entry.getKey());
        }
        for (PolicyDecisionKey policyDecisionKey : isAllowedPolicyDecisionsCache.asMap().keySet()) {
            addCacheKey(cachedPrincipals, policyDecisionKey.getSubject(), policyDecisionKey);
        }
        return cachedPrincipals;
    }

    private void addCacheKey(Map<Principal, List<AalCacheKey>> cachedPrincipals,
                             Principal principal, AalCacheKey cacheKey) {
        List<AalCacheKey> cacheKeys = cachedPrincipals.get(principal);
        if (cacheKeys == null) {
            cacheKeys = new ArrayList<>();
            cachedPrincipals.put(principal, cacheKeys);
        }
        cacheKeys.add(cacheKey);
    }

    private void fireOnInvalidate(final Principal principal) {
        for (PrincipalEventListener eventListener : principalEventListeners) {
            eventListener.onInvalidate(principal);
        }
    }
}
