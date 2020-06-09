package com.rooxteam.uidm.sdk.servlet.filter;

import com.rooxteam.sso.aal.AuthenticationAuthorizationLibrary;
import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.PrincipalImpl;
import com.rooxteam.uidm.sdk.servlet.configuration.ServletFilterConfigurationForTesting;
import com.rooxteam.uidm.sdk.servlet.service.ServletAuthFilterService;
import com.rooxteam.uidm.sdk.servlet.service.ServletAuthFilterServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TreeMap;

import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class ServletAuthFilterTest {

    private ServletAuthFilter getFilter(String principalId) {
        FilterConfig filterConfig = new ServletFilterConfigurationForTesting();
        String token = "sdfsdfsfds";
        Map<String, Object> sharedIdentityProperties = new TreeMap<String, Object>();
        sharedIdentityProperties.put("scopes", Arrays.asList("scope1", "scope2"));
        sharedIdentityProperties.put("roles", Arrays.asList("role1", "role2"));
        sharedIdentityProperties.put("prn", principalId);
        sharedIdentityProperties.put("authLevel", Arrays.asList("5"));
        Calendar expirationTime = new GregorianCalendar();
        Principal principal = new PrincipalImpl(token, sharedIdentityProperties, expirationTime);

        AuthenticationAuthorizationLibrary aal = Mockito.mock(AuthenticationAuthorizationLibrary.class);
        when(aal.authenticate(anyMap())).thenReturn(principal);
        when(aal.validate(anyString())).thenReturn(principal);

        ServletAuthFilterService servletAuthFilterHelper = new ServletAuthFilterServiceImpl(filterConfig, aal);

        ServletAuthFilter servletAuthFilter = new ServletAuthFilter();
        servletAuthFilter.init(new ServletFilterConfigurationForTesting(), servletAuthFilterHelper);

        return servletAuthFilter;
    }

    @Test
    public void test_filter() throws Exception {
        String principalId = "12999090909090";
        String token1 = "sdfsdfsfds";
        String token2 = "gfdyueefds";
        Cookie[] cookies1 = new Cookie[2];
        cookies1[0] = new Cookie("at", token1);
        cookies1[1] = new Cookie("test2", "test2");

        Cookie[] cookies2 = new Cookie[1];
        cookies2[0] = new Cookie("tet", token1);

        ServletAuthFilter servletAuthFilter = getFilter(principalId);

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        when(request.getCookies()).thenReturn(cookies1);
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(new ArrayList()));

        Chain chain1 = new Chain();
        servletAuthFilter.doFilter(request, response, chain1);
        Assert.assertEquals(principalId, chain1.servletRequest.getRemoteUser());

        HttpServletRequest request2 = Mockito.mock(HttpServletRequest.class);
        when(request2.getCookies()).thenReturn(cookies2);
        when(request2.getHeader(eq("Authorization"))).thenReturn("Bearer " + token2);
        when(request2.getHeaderNames()).thenReturn(Collections.enumeration(new ArrayList()));
        Chain chain2 = new Chain();
        servletAuthFilter.doFilter(request2, response, chain2);
        Assert.assertEquals(principalId, chain2.servletRequest.getRemoteUser());
    }

    private static class Chain implements FilterChain {
        HttpServletResponse servletResponse = null;
        HttpServletRequest servletRequest = null;

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
            this.servletResponse = (HttpServletResponse) servletResponse;
            this.servletRequest = (HttpServletRequest) servletRequest;
        }
    }
}
