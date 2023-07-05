package com.rooxteam.uidm.sdk.spring.authentication;

import com.rooxteam.uidm.sdk.spring.configuration.UidmSpringSecurityFilterConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        UidmUserPreAuthenticationFilterTest.TestConfiguration.class
})
public class UidmUserPreAuthenticationFilterTest {

    @Autowired
    private GenericFilterBean uidmUserPreAuthenticationFilter;

    @Autowired
    private SsoAuthorizationClient ssoAuthorizationClient;

    @Autowired
    private UserPreAuthFilterSettings userPreAuthFilterSettings;


    @Before
    public void setUp() {
        reset(ssoAuthorizationClient);
        reset(userPreAuthFilterSettings);
    }

    @Test
    public void test_versionedAuthHeaderIsUsedInFilter() throws ServletException, IOException {
        test_authHeader("Bearer sso_1.0_%s", "test-auth-token");
    }

    @Test
    public void test_rawTokenAuthHeaderIsUsedInFilter() throws ServletException, IOException {
        test_authHeader("Bearer %s", "test-auth-token");
    }

    private void test_authHeader(String headerPrefixFormat, String authToken) throws ServletException, IOException {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION, String.format("Bearer %s", authToken));

        final MockHttpServletResponse response = new MockHttpServletResponse();

        when(ssoAuthorizationClient.getPreAuthenticatedUserState((HttpServletRequest) any(), eq(authToken))).thenReturn(mockAuthState(authToken));

        uidmUserPreAuthenticationFilter.doFilter(request, response, new MockFilterChain());

        verify(ssoAuthorizationClient, atLeastOnce()).getPreAuthenticatedUserState(Matchers.<HttpServletRequest>any(), eq(authToken));

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertEquals(authToken, auth.getCredentials());
        assertEquals("test-principal", auth.getPrincipal());
    }

    @Test
    public void test_versionedAuthHeaderWrongVersionFails() throws ServletException, IOException {
        final String authToken = "test-auth-token";
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION, String.format("Bearer sso_2.0_%s", authToken));

        final MockHttpServletResponse response = new MockHttpServletResponse();

        when(ssoAuthorizationClient.getPreAuthenticatedUserState((HttpServletRequest) any(), eq(authToken))).thenReturn(mockAuthState(authToken));
        SecurityContextHolder.getContext().setAuthentication(null);

        uidmUserPreAuthenticationFilter.doFilter(request, response, new MockFilterChain());

        verify(ssoAuthorizationClient, never()).getPreAuthenticatedUserState(Matchers.<HttpServletRequest>any(), Matchers.<String>any());

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
    }

    @Test
    public void test_versionedAuthHeaderUnknownPrefixFails() throws ServletException, IOException {
        final String authToken = "test-auth-token";
        final String prefixedAuthToken = "prefix_" + authToken;

        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION, String.format("Bearer %s", prefixedAuthToken));

        final MockHttpServletResponse response = new MockHttpServletResponse();

        when(ssoAuthorizationClient.getPreAuthenticatedUserState((HttpServletRequest) any(), eq(authToken))).thenReturn(mockAuthState(authToken));
        SecurityContextHolder.getContext().setAuthentication(null);

        uidmUserPreAuthenticationFilter.doFilter(request, response, new MockFilterChain());

        verify(ssoAuthorizationClient, atLeastOnce()).getPreAuthenticatedUserState(Matchers.<HttpServletRequest>any(), eq(prefixedAuthToken));

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
    }

    @Test
    public void test_tokenNameFromConfigurationIsUsedInFilter() throws ServletException, IOException {
        final String cookieName = "test-token-cookie";
        final String authToken = "test-auth-token";

        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(cookieName, authToken));

        final MockHttpServletResponse response = new MockHttpServletResponse();

        when(userPreAuthFilterSettings.getCookieName()).thenReturn(cookieName);
        when(ssoAuthorizationClient.getPreAuthenticatedUserState((HttpServletRequest) any(), eq(authToken))).thenReturn(mockAuthState(authToken));

        uidmUserPreAuthenticationFilter.doFilter(request, response, new MockFilterChain());

        verify(userPreAuthFilterSettings, atLeastOnce()).getCookieName();
        verify(ssoAuthorizationClient, atLeastOnce()).getPreAuthenticatedUserState(Matchers.<HttpServletRequest>any(), eq(authToken));

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertEquals(authToken, auth.getCredentials());
        assertEquals("test-principal", auth.getPrincipal());
    }

    @Test
    public void test_principalPropertiesNameFromConfigurationIsUsedInFilter() throws ServletException, IOException {
        final String cookieName = "test-token-cookie";
        final String authToken = "test-auth-token";

        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(cookieName, authToken));

        final MockHttpServletResponse response = new MockHttpServletResponse();

        // NOTE: explicitly set "NULL" value defines contract for the filter to handle such Configurations
        when(userPreAuthFilterSettings.getPrincipalAttributesExposedToMDC()).thenReturn(null);

        when(userPreAuthFilterSettings.getCookieName()).thenReturn(cookieName);
        when(ssoAuthorizationClient.getPreAuthenticatedUserState((HttpServletRequest) any(), eq(authToken))).thenReturn(mockAuthState(authToken));

        uidmUserPreAuthenticationFilter.doFilter(request, response, new MockFilterChain());

        verify(ssoAuthorizationClient, atLeastOnce()).getPreAuthenticatedUserState(Matchers.<HttpServletRequest>any(), eq(authToken));
        verify(userPreAuthFilterSettings, atLeastOnce()).getPrincipalAttributesExposedToMDC();
    }

    private AuthenticationState mockAuthState(String authToken) {
        final Authentication auth = new MockAuthentication("test-principal", authToken);
        return new AuthenticationState(auth);
    }


    static class MockAuthentication extends AbstractAuthenticationToken {
        private final String principal;
        private final String credentials;

        public MockAuthentication(String principal, String credentials) {
            super(null);

            this.principal = principal;
            this.credentials = credentials;
        }


        @Override
        public Object getCredentials() {
            return credentials;
        }

        @Override
        public Object getPrincipal() {
            return principal;
        }
    }

    @Configuration
    @Import({ UidmSpringSecurityFilterConfiguration.class })
    static class TestConfiguration {

        @Bean
        @Primary
        public SsoAuthorizationClient ssoAuthorizationClient() {
            return mock(SsoAuthorizationClient.class);
        }

        @Bean
        @Primary
        public UserPreAuthFilterSettings userPreAuthFilterSettings() {
            return mock(UserPreAuthFilterSettings.class);
        }
    }
}
