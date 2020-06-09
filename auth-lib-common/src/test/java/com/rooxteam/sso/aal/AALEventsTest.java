package com.rooxteam.sso.aal;

import com.google.common.cache.Cache;
import com.rooxteam.sso.aal.client.SsoAuthenticationClient;
import com.rooxteam.sso.aal.client.SsoAuthorizationClient;
import com.rooxteam.sso.aal.client.model.EvaluationResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.rooxteam.sso.aal.AALInvalidationTest.IP_229_213_38_0;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class AALEventsTest {

    public static final long DEFAULT_TIMEOUT = 0;
    public static final TimeUnit DEFAULT_TIMEUNIT = TimeUnit.MILLISECONDS;
    private RooxAuthenticationAuthorizationLibrary aal;

    private final PrincipalEventListener mockListener = mock(PrincipalEventListener.class);
    private final SsoAuthenticationClient mockSsoAuthenticationClient = mock(SsoAuthenticationClient.class);
    private final SsoAuthorizationClient mockSsoAuthorizationClient = mock(SsoAuthorizationClient.class);
    private final Cache<PrincipalKey, Principal> mockPrincipalCache = (Cache<PrincipalKey, Principal>) mock(Cache.class);
    private final Cache<PolicyDecisionKey, EvaluationResponse> mockPolicyDecisionsCache = mock(Cache.class);

    @Before
    public void setUp() {
        reset(mockSsoAuthenticationClient, mockSsoAuthorizationClient, mockPrincipalCache, mockPolicyDecisionsCache, mockListener);
        aal = new RooxAuthenticationAuthorizationLibrary(
                null,null, mockSsoAuthorizationClient, mockSsoAuthenticationClient, null, null,
                mockPolicyDecisionsCache, mockPrincipalCache, null, AuthorizationType.JWT);
        aal.addPrincipalListener(mockListener);
    }

    @After
    public void tearDown() {
        aal.removePrincipalListener(mockListener);
    }

    @Test
    public void invalidate_principal_should_fire_onInvalidate_event() {
        Principal mockPrincipal = mock(Principal.class);
        ConcurrentHashMap<PrincipalKey, Principal> PrincipalCacheMap = new ConcurrentHashMap<PrincipalKey, Principal>();
        PrincipalKey PrincipalKey = new PrincipalKey(AuthParamType.IP, IP_229_213_38_0);
        PrincipalCacheMap.put(PrincipalKey, mockPrincipal);
        when(mockPrincipalCache.asMap())
                .thenReturn(PrincipalCacheMap);
        when(mockPolicyDecisionsCache.asMap())
                .thenReturn(new ConcurrentHashMap<PolicyDecisionKey, EvaluationResponse>());

        aal.invalidate(mockPrincipal);
        verify(mockListener, times(1)).onInvalidate(mockPrincipal);
        verify(mockListener, times(0)).onAuthenticate(any(Principal.class));
        verify(mockListener, times(0)).onRequestPrincipal(any(Principal.class));
        verify(mockPrincipalCache, times(1)).invalidate(PrincipalKey);
    }

    @Test
    public void should_NOT_fire_onRequestPrincipal_event_if_jwt_is_not_valid() {
        final String jwtToken = "jwt token";
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getJwtToken()).thenReturn(jwtToken);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(SsoAuthenticationClient.JWT_PARAM_NAME, jwtToken);
        params.put(SsoAuthenticationClient.UPDATE_LIFE_TIME_PARAM, true);
        when(mockSsoAuthenticationClient.authenticate(params))
                .thenReturn(null);

        Principal principal = aal.renew(mockPrincipal, true, DEFAULT_TIMEOUT, DEFAULT_TIMEUNIT);
        assertNull(principal);
        verify(mockListener, times(0)).onInvalidate(any(Principal.class));
        verify(mockListener, times(0)).onAuthenticate(any(Principal.class));
        verify(mockListener, times(0)).onRequestPrincipal(any(Principal.class));
        verify(mockPrincipal, times(1)).getJwtToken();
    }

}
