package com.rooxteam.uidm.sdk.servlet.filter;

import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.PrincipalImpl;
import com.rooxteam.sso.aal.client.CommonSsoAuthorizationClient;
import com.rooxteam.udim.sdk.servlet.filter.ServletFilter;
import com.rooxteam.udim.sdk.servlet.testing.ServletFilterConfigurationForTesting;
import lombok.Getter;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.FilterChain;
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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class ServletFilterTest {

    @Test
    public void test_servlet() throws Exception {
        String principalId = "12999090909090";
        String token = "sdfsdfsfds";
        Cookie[] cookies1 = new Cookie[2];
        cookies1[0] = new Cookie("at", token);
        cookies1[1] = new Cookie("test2", "test2");
        Map<String, Object> sharedIdentityProperties = new TreeMap<>();
        sharedIdentityProperties.put("scopes", Arrays.asList("scope1", "scope2"));
        sharedIdentityProperties.put("roles", Arrays.asList("role1", "role2"));
        sharedIdentityProperties.put("prn", principalId);
        sharedIdentityProperties.put("authLevel", Arrays.asList("5"));
        Calendar expirationTime = new GregorianCalendar();
        Principal principal = new PrincipalImpl(token, sharedIdentityProperties, expirationTime);

        ServletFilter servletFilter = new ServletFilter();
        CommonSsoAuthorizationClient ssoAuthorizationClient = Mockito.mock(CommonSsoAuthorizationClient.class);
        when(ssoAuthorizationClient.validate(any(), any())).thenReturn(principal);
        servletFilter.finishInit(ssoAuthorizationClient, new ServletFilterConfigurationForTesting());

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        when(request.getCookies()).thenReturn(cookies1);
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(new ArrayList<>()));

        Chain chain = new Chain();
        servletFilter.doFilter(request, response, chain);
        Assert.assertEquals(principalId, chain.getServletRequest().getRemoteUser());
    }

    @Getter
    private static class Chain implements FilterChain {
        private HttpServletResponse servletResponse = null;
        private HttpServletRequest servletRequest = null;

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
            this.servletResponse = (HttpServletResponse) servletResponse;
            this.servletRequest = (HttpServletRequest) servletRequest;
        }
    }
}
