package com.rooxteam.sso.aal;

import com.rooxteam.sso.aal.client.SsoAuthenticationClient;
import com.rooxteam.sso.aal.client.SsoAuthorizationClient;
import com.rooxteam.sso.aal.client.model.Decision;
import com.rooxteam.sso.aal.client.model.EvaluationResponse;
import com.rooxteam.sso.aal.metrics.NoOpMetricsIntegration;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class AALIsAllowedTest {

    public static final long DEFAULT_TIMEOUT = 0;
    public static final TimeUnit DEFAULT_TIMEUNIT = TimeUnit.MILLISECONDS;
    private RooxAuthenticationAuthorizationLibrary aal;
    private final SsoAuthorizationClient mockSsoAuthorizationClient = mock(SsoAuthorizationClient.class);
    private final SsoAuthenticationClient mockSsoAuthenticationClient = mock(SsoAuthenticationClient.class);

    @Before
    public void setUp() {
        reset(mockSsoAuthorizationClient, mockSsoAuthenticationClient);
        aal = new RooxAuthenticationAuthorizationLibrary(null, null, mockSsoAuthorizationClient, mockSsoAuthenticationClient,
                null, null, null,
                new NoOpMetricsIntegration());
    }


    @Test
    public void aal_should_allow_test_request() {
        Principal mockPrincipal = mock(Principal.class);
        final String testToken = "Test JWT Token";
        when(mockPrincipal.getJwtToken())
                .thenReturn(testToken);
//        when(mockSsoAuthorizationClient.validate(testToken))
//                .thenReturn(mockSsoToken);
        when(mockSsoAuthorizationClient.isActionOnResourceAllowedByPolicy(mockPrincipal, "/TestResource", "GET", Collections.EMPTY_MAP))
                .thenReturn(new EvaluationResponse(Decision.Permit));

        Map<String, Object> envParameters = Collections.emptyMap();
        boolean isAllowed = aal.isAllowed(mockPrincipal, "/TestResource", "GET", envParameters, DEFAULT_TIMEOUT, DEFAULT_TIMEUNIT);
        assertTrue(isAllowed);

        verify(mockSsoAuthorizationClient, times(1)).isActionOnResourceAllowedByPolicy(mockPrincipal, "/TestResource", "GET", Collections.EMPTY_MAP);
    }

    @Test
    public void aal_should_NOT_allow_test_request() {
        Principal mockPrincipal = mock(Principal.class);
        final String testToken = "Test JWT Token";
        when(mockPrincipal.getJwtToken())
                .thenReturn(testToken);
//        when(mockSsoAuthorizationClient.authenticate(testToken))
//                .thenReturn(mockSsoToken);
        when(mockSsoAuthorizationClient.isActionOnResourceAllowedByPolicy(mockPrincipal, "/TestResource", "GET", Collections.EMPTY_MAP))
                .thenReturn(new EvaluationResponse(Decision.Deny));

        Map<String, Object> envParameters = Collections.emptyMap();
        boolean isAllowed = aal.isAllowed(mockPrincipal, "/TestResource", "GET", envParameters, DEFAULT_TIMEOUT, DEFAULT_TIMEUNIT);
        assertFalse(isAllowed);

        verify(mockSsoAuthorizationClient, times(1)).isActionOnResourceAllowedByPolicy(mockPrincipal, "/TestResource", "GET", Collections.EMPTY_MAP);
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
