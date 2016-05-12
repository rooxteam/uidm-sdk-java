package com.rooxteam.sso.aal.client;

import com.iplanet.sso.SSOToken;
import com.sun.identity.authentication.AuthContext;
import com.sun.identity.authentication.spi.AuthLoginException;
import com.sun.identity.shared.locale.L10NMessageImpl;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class AuthorizationClientTest {

    @Test
    public void authenticateByJwt_invalidJWTToken_NullSsoToken() throws L10NMessageImpl, AuthLoginException {
        final AuthContext mockAuthContext = mock(AuthContext.class);
        Configuration config = new BaseConfiguration();
        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        SsoAuthorizationClient client = new SsoAuthorizationClient(config, httpClient) {
            @Override
            protected AuthContext initAuthContext() {
                return mockAuthContext;
            }
        };
        SSOToken token = mock(SSOToken.class);
        when(mockAuthContext.getStatus()).thenReturn(AuthContext.Status.SUCCESS);
        when(mockAuthContext.getSSOToken()).thenReturn(token);

        SSOToken ssoToken = client.authenticateByJwt("invalid token");

        assertNotNull(ssoToken);
        assertEquals(token, ssoToken);
        verify(mockAuthContext, times(1)).login(any(AuthContext.IndexType.class), anyString(), any(String[].class));
    }
}
