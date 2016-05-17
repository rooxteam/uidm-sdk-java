package com.rooxteam.sso.aal;

import com.google.common.cache.Cache;
import com.iplanet.sso.SSOToken;
import com.rooxteam.sso.aal.client.SsoAuthenticationClient;
import com.rooxteam.sso.aal.client.SsoAuthorizationClient;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.rooxteam.sso.aal.AALInvalidationTest.IP_229_213_38_0;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class AALIsAllowedTest {

    public static final long DEFAULT_TIMEOUT = 0;
    public static final TimeUnit DEFAULT_TIMEUNIT = TimeUnit.MILLISECONDS;
    private RooxAuthenticationAuthorizationLibrary aal;
    private final Cache<PolicyDecisionKey, Boolean> mockPolicyDecisionsCache = mock(Cache.class);
    private final Cache<PrincipalKey, Principal> mockPrincipalCache = (Cache<PrincipalKey, Principal>) mock(Cache.class);
    private final SsoAuthorizationClient mockSsoAuthorizationClient = mock(SsoAuthorizationClient.class);
    private final SsoAuthenticationClient mockSsoAuthenticationClient = mock(SsoAuthenticationClient.class);

    @Before
    public void setUp() {
        reset(mockPolicyDecisionsCache, mockPrincipalCache, mockSsoAuthorizationClient, mockSsoAuthenticationClient);
        aal = new RooxAuthenticationAuthorizationLibrary(null, mockSsoAuthorizationClient, mockSsoAuthenticationClient,
                null, null, mockPolicyDecisionsCache, mockPrincipalCache, null, AuthorizationType.SSO_TOKEN);
    }


    @Test
    public void aal_should_allow_test_request() {
        Principal mockPrincipal = mock(Principal.class);
        SSOToken mockSsoToken = mock(SSOToken.class);
        final String testToken = "Test JWT Token";
        when(mockPrincipal.getJwtToken())
                .thenReturn(testToken);
        when(mockSsoAuthorizationClient.authenticateByJwt(testToken))
                .thenReturn(mockSsoToken);
        when(mockSsoAuthorizationClient.isActionOnResourceAllowedByPolicy(mockSsoToken, "/TestResource", "GET"))
                .thenReturn(true);

        Map<String, Object> envParameters = Collections.emptyMap();
        boolean isAllowed = aal.isAllowed(mockPrincipal, "/TestResource", "GET", envParameters, DEFAULT_TIMEOUT, DEFAULT_TIMEUNIT);
        assertTrue(isAllowed);

        verify(mockSsoAuthorizationClient, times(1)).authenticateByJwt(testToken);
        verify(mockSsoAuthorizationClient, times(1)).isActionOnResourceAllowedByPolicy(mockSsoToken, "/TestResource", "GET");
        verify(mockPolicyDecisionsCache, times(1)).getIfPresent(any());
        verify(mockPolicyDecisionsCache, times(1)).put(any(PolicyDecisionKey.class), eq(true));
    }

    @Test
    public void aal_should_NOT_allow_test_request() {
        Principal mockPrincipal = mock(Principal.class);
        SSOToken mockSsoToken = mock(SSOToken.class);
        final String testToken = "Test JWT Token";
        when(mockPrincipal.getJwtToken())
                .thenReturn(testToken);
        when(mockSsoAuthorizationClient.authenticateByJwt(testToken))
                .thenReturn(mockSsoToken);
        when(mockSsoAuthorizationClient.isActionOnResourceAllowedByPolicy(mockSsoToken, "/TestResource", "GET"))
                .thenReturn(false);

        Map<String, Object> envParameters = Collections.emptyMap();
        boolean isAllowed = aal.isAllowed(mockPrincipal, "/TestResource", "GET", envParameters, DEFAULT_TIMEOUT, DEFAULT_TIMEUNIT);
        assertFalse(isAllowed);

        verify(mockSsoAuthorizationClient, times(1)).authenticateByJwt(testToken);
        verify(mockSsoAuthorizationClient, times(1)).isActionOnResourceAllowedByPolicy(mockSsoToken, "/TestResource", "GET");
        verify(mockPolicyDecisionsCache, times(1)).getIfPresent(any());
        verify(mockPolicyDecisionsCache, times(1)).put(any(PolicyDecisionKey.class), eq(false));
    }

    @Test
    public void aal_should_allow_test_request_after_invalidation() {
        Principal mockPrincipal = mock(Principal.class);
        SSOToken mockSsoToken = mock(SSOToken.class);
        final String testToken = "Test JWT Token";
        when(mockPrincipal.getJwtToken())
                .thenReturn(testToken);
        when(mockSsoAuthorizationClient.authenticateByJwt(testToken))
                .thenReturn(mockSsoToken);
        when(mockSsoAuthorizationClient.isActionOnResourceAllowedByPolicy(mockSsoToken, "/TestResource", "GET"))
                .thenReturn(true);
        ConcurrentHashMap<PrincipalKey, Principal> PrincipalCacheMap = new ConcurrentHashMap<>();
        PrincipalKey PrincipalKey = new PrincipalKey(AuthParamType.IP, IP_229_213_38_0);
        PrincipalCacheMap.put(PrincipalKey, mockPrincipal);
        when(mockPrincipalCache.asMap())
                .thenReturn(PrincipalCacheMap);
        when(mockPolicyDecisionsCache.asMap())
                .thenReturn(new ConcurrentHashMap<PolicyDecisionKey, Boolean>());

        Map<String, Object> envParameters = Collections.emptyMap();
        boolean isAllowed = aal.isAllowed(mockPrincipal, "/TestResource", "GET", envParameters, DEFAULT_TIMEOUT, DEFAULT_TIMEUNIT);
        assertTrue(isAllowed);
        aal.invalidate(mockPrincipal);
        isAllowed = aal.isAllowed(mockPrincipal, "/TestResource", "GET", envParameters, DEFAULT_TIMEOUT, DEFAULT_TIMEUNIT);
        assertTrue(isAllowed);

        verify(mockSsoAuthorizationClient, times(2)).authenticateByJwt(testToken);
        verify(mockSsoAuthorizationClient, times(2)).isActionOnResourceAllowedByPolicy(mockSsoToken, "/TestResource", "GET");
        verify(mockPolicyDecisionsCache, times(2)).getIfPresent(any());
        verify(mockPolicyDecisionsCache, times(2)).put(any(PolicyDecisionKey.class), eq(true));
    }


    @Test
    public void aal_should_use_policy_decision_cache_to_allow_test_request() {
        Principal mockPrincipal = mock(Principal.class);
        PolicyDecisionKey key = new PolicyDecisionKey(mockPrincipal, "/TestResource", "GET");
        when(mockPolicyDecisionsCache.getIfPresent(key))
                .thenReturn(true);
        ConcurrentHashMap<PrincipalKey, Principal> PrincipalCacheMap = new ConcurrentHashMap<>();
        PrincipalKey PrincipalKey = new PrincipalKey(AuthParamType.IP, IP_229_213_38_0);
        PrincipalCacheMap.put(PrincipalKey, mockPrincipal);
        when(mockPrincipalCache.asMap())
                .thenReturn(PrincipalCacheMap);

        Map<String, Object> envParameters = Collections.emptyMap();
        boolean isAllowed = aal.isAllowed(mockPrincipal, "/TestResource", "GET", envParameters, DEFAULT_TIMEOUT, DEFAULT_TIMEUNIT);
        assertTrue(isAllowed);

        verify(mockSsoAuthorizationClient, times(0)).authenticateByJwt(anyString());
        verify(mockSsoAuthorizationClient, times(0)).isActionOnResourceAllowedByPolicy(any(SSOToken.class), anyString(), anyString());
        verify(mockPolicyDecisionsCache, times(1)).getIfPresent(key);
    }

    @Test
    public void aal_should_use_policy_decision_cache_to_disallow_test_request() {
        Principal mockPrincipal = mock(Principal.class);
        PolicyDecisionKey key = new PolicyDecisionKey(mockPrincipal, "/TestResource", "GET");
        when(mockPolicyDecisionsCache.getIfPresent(key))
                .thenReturn(false);
        ConcurrentHashMap<PrincipalKey, Principal> PrincipalCacheMap = new ConcurrentHashMap<>();
        PrincipalKey PrincipalKey = new PrincipalKey(AuthParamType.IP, IP_229_213_38_0);
        PrincipalCacheMap.put(PrincipalKey, mockPrincipal);
        when(mockPrincipalCache.asMap())
                .thenReturn(PrincipalCacheMap);

        Map<String, Object> envParameters = Collections.emptyMap();
        boolean isAllowed = aal.isAllowed(mockPrincipal, "/TestResource", "GET", envParameters, DEFAULT_TIMEOUT, DEFAULT_TIMEUNIT);
        assertFalse(isAllowed);

        verify(mockSsoAuthorizationClient, times(0)).authenticateByJwt(anyString());
        verify(mockSsoAuthorizationClient, times(0)).isActionOnResourceAllowedByPolicy(any(SSOToken.class), anyString(), anyString());
        verify(mockPolicyDecisionsCache, times(1)).getIfPresent(key);
    }

    @Test
    public void aal_should_reset_policy_decision_from_cache() {
        Principal mockPrincipal = mock(Principal.class);
        PolicyDecisionKey key = new PolicyDecisionKey(mockPrincipal, "/TestResource", "GET");
        ConcurrentHashMap<PolicyDecisionKey, Boolean> policyDecisionsCacheMap = new ConcurrentHashMap<>();
        policyDecisionsCacheMap.put(key, true);
        when(mockPolicyDecisionsCache.asMap())
                .thenReturn(policyDecisionsCacheMap);

        aal.resetPolicies(mockPrincipal);

        verify(mockPolicyDecisionsCache, times(1)).invalidate(key);
    }

    @Test
    public void aal_should_reset_policy_decision_from_cache_on_invalidate() {
        Principal mockPrincipal = mock(Principal.class);
        PolicyDecisionKey key = new PolicyDecisionKey(mockPrincipal, "/TestResource", "GET");
        ConcurrentHashMap<PolicyDecisionKey, Boolean> policyDecisionsCacheMap = new ConcurrentHashMap<>();
        policyDecisionsCacheMap.put(key, true);
        when(mockPolicyDecisionsCache.asMap())
                .thenReturn(policyDecisionsCacheMap);
        when(mockPrincipalCache.asMap())
                .thenReturn(new ConcurrentHashMap<PrincipalKey, Principal>());

        aal.invalidate(mockPrincipal);

        verify(mockPolicyDecisionsCache, times(1)).invalidate(key);
    }

    @Test(expected = IllegalArgumentException.class)
    public void aal_should_throw_illegalArgumentException_when_principal_is_null() {
        try {
            aal.resetPolicies(null);
        } catch (IllegalArgumentException e) {
            assertEquals("Principal argument is missing.", e.getMessage());
            throw e;
        }
    }
}
