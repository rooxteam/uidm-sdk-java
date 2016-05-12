package com.rooxteam.sso.aal;

import com.google.common.cache.Cache;
import com.rooxteam.sso.aal.client.SsoAuthenticationClient;
import com.rooxteam.sso.aal.client.SsoAuthorizationClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.rooxteam.sso.aal.AALInvalidationTest.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class AALEventsTest {

    public static final long DEFAULT_TIMEOUT = 0;
    public static final TimeUnit DEFAULT_TIMEUNIT = TimeUnit.MILLISECONDS;
    private RooxAuthenticationAuthorizationLibrary aal;

    private final PrincipalEventListener mockListener = mock(PrincipalEventListener.class);
    private final SsoAuthenticationClient mockSsoAuthenticationClient = mock(SsoAuthenticationClient.class);
    private final SsoAuthorizationClient mockSsoAuthorizationClient = mock(SsoAuthorizationClient.class);
    private final Cache<YotaPrincipalKey, YotaPrincipal> mockPrincipalCache = (Cache<YotaPrincipalKey, YotaPrincipal>) mock(Cache.class);
    private final Cache<PolicyDecisionKey, Boolean> mockPolicyDecisionsCache = (Cache<PolicyDecisionKey, Boolean>) mock(Cache.class);

    @Before
    public void setUp() {
        reset(mockSsoAuthenticationClient, mockSsoAuthorizationClient, mockPrincipalCache, mockPolicyDecisionsCache, mockListener);
        aal = new RooxAuthenticationAuthorizationLibrary(
                null, mockSsoAuthorizationClient, mockSsoAuthenticationClient, null, null,
                mockPolicyDecisionsCache, mockPrincipalCache, null, AuthorizationType.SSO_TOKEN);
        aal.addPrincipalListener(mockListener);
    }

    @After
    public void tearDown() {
        aal.removePrincipalListener(mockListener);
    }

    @Test
    public void authenticate_should_fire_onAuthenticate_event() {
        Map<String,Object> params = new HashMap<>();
        params.put(SsoAuthenticationClient.IP, IP_229_213_38_0);
        when(mockSsoAuthenticationClient.authenticate(params))
                .thenReturn(TOKEN_FOR_229_213_38_0);

        params = new HashMap<>();
        params.put(SsoAuthenticationClient.IP, IP_229_213_38_0);

        YotaPrincipal authenticate = aal.authenticate(params, DEFAULT_TIMEOUT, DEFAULT_TIMEUNIT);
        assertNotNull(authenticate);
        verify(mockListener, times(1)).onAuthenticate(authenticate);
        verify(mockListener, times(0)).onInvalidate(any(YotaPrincipal.class));
        verify(mockListener, times(0)).onRequestPrincipal(any(YotaPrincipal.class));
    }

    @Test
    public void authenticate_should_NOT_fire_onAuthenticate_event_if_ip_not_in_pool() {

        Map<String, String> params = new HashMap<>();
        params.put(AuthParamType.IP.getValue(), IP_229_213_38_0);

        when(mockSsoAuthenticationClient.authenticate(params))
                .thenReturn(TOKEN_FOR_229_213_38_0);

        params = new HashMap<>();
        params.put(AuthParamType.IP.getValue(), IP_229_213_38_1);

        YotaPrincipal authenticate = aal.authenticate(params, DEFAULT_TIMEOUT, DEFAULT_TIMEUNIT);
        assertNull(authenticate);
        verify(mockListener, times(0)).onAuthenticate(any(YotaPrincipal.class));
        verify(mockListener, times(0)).onInvalidate(any(YotaPrincipal.class));
        verify(mockListener, times(0)).onRequestPrincipal(any(YotaPrincipal.class));
    }

    @Test
    public void invalidate_principal_should_fire_onInvalidate_event() {
        YotaPrincipal mockYotaPrincipal = mock(YotaPrincipal.class);
        ConcurrentHashMap<YotaPrincipalKey, YotaPrincipal> yotaPrincipalCacheMap = new ConcurrentHashMap<>();
        YotaPrincipalKey yotaPrincipalKey = new YotaPrincipalKey(AuthParamType.IP, IP_229_213_38_0);
        yotaPrincipalCacheMap.put(yotaPrincipalKey, mockYotaPrincipal);
        when(mockPrincipalCache.asMap())
                .thenReturn(yotaPrincipalCacheMap);
        when(mockPolicyDecisionsCache.asMap())
                .thenReturn(new ConcurrentHashMap<PolicyDecisionKey, Boolean>());

        aal.invalidate(mockYotaPrincipal);
        verify(mockListener, times(1)).onInvalidate(mockYotaPrincipal);
        verify(mockListener, times(0)).onAuthenticate(any(YotaPrincipal.class));
        verify(mockListener, times(0)).onRequestPrincipal(any(YotaPrincipal.class));
        verify(mockPrincipalCache, times(1)).invalidate(yotaPrincipalKey);
    }

    @Test
    public void request_principal_should_fire_onRequestPrincipal_event() {
        final String jwtToken = "jwt token";
        YotaPrincipal mockYotaPrincipal = mock(YotaPrincipal.class);
        when(mockYotaPrincipal.getJwtToken()).thenReturn(jwtToken);
        Map<String,Object> params = new HashMap<>();
        params.put(SsoAuthenticationClient.JWT_PARAM_NAME, jwtToken);
        params.put(SsoAuthenticationClient.UPDATE_LIFE_TIME_PARAM, true);
        when(mockSsoAuthenticationClient.authenticate(params))
                .thenReturn(TOKEN_FOR_229_213_38_0);

        YotaPrincipal yotaPrincipal = aal.renew(mockYotaPrincipal, true, DEFAULT_TIMEOUT, DEFAULT_TIMEUNIT);
        assertNotNull(yotaPrincipal);
        assertNotSame(yotaPrincipal, mockYotaPrincipal);
        verify(mockListener, times(0)).onInvalidate(any(YotaPrincipal.class));
        verify(mockListener, times(0)).onAuthenticate(any(YotaPrincipal.class));
        verify(mockListener, times(1)).onRequestPrincipal(yotaPrincipal);
        verify(mockYotaPrincipal, times(1)).getJwtToken();
    }


    @Test
    public void should_NOT_fire_onRequestPrincipal_event_if_jwt_is_not_valid() {
        final String jwtToken = "jwt token";
        YotaPrincipal mockYotaPrincipal = mock(YotaPrincipal.class);
        when(mockYotaPrincipal.getJwtToken()).thenReturn(jwtToken);
        Map<String,Object> params = new HashMap<>();
        params.put(SsoAuthenticationClient.JWT_PARAM_NAME, jwtToken);
        params.put(SsoAuthenticationClient.UPDATE_LIFE_TIME_PARAM, true);
        when(mockSsoAuthenticationClient.authenticate(params))
                .thenReturn(null);

        YotaPrincipal yotaPrincipal = aal.renew(mockYotaPrincipal, true, DEFAULT_TIMEOUT, DEFAULT_TIMEUNIT);
        assertNull(yotaPrincipal);
        verify(mockListener, times(0)).onInvalidate(any(YotaPrincipal.class));
        verify(mockListener, times(0)).onAuthenticate(any(YotaPrincipal.class));
        verify(mockListener, times(0)).onRequestPrincipal(any(YotaPrincipal.class));
        verify(mockYotaPrincipal, times(1)).getJwtToken();
    }

}
