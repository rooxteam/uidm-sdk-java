package com.rooxteam.uidm.sdk.servlet.filter;

import com.rooxteam.sso.aal.Principal;
import com.rooxteam.uidm.sdk.servlet.AuthFilterLogger;
import com.rooxteam.uidm.sdk.servlet.configuration.FilterConfigKeys;
import com.rooxteam.uidm.sdk.servlet.service.ServletAuthFilterService;
import com.rooxteam.uidm.sdk.servlet.service.ServletAuthFilterServiceImpl;
import com.rooxteam.uidm.sdk.servlet.util.LoggerUtils;
import com.rooxteam.uidm.sdk.servlet.util.ParseStringUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * This filter attempts to authenticate the user by Cookie or, if absent, by Authorization header.
 * If the user can not be authenticated, then the user is redirected to authentication endpoint.
 * On successful authentication some claims from access token are placed in the request and forwarded.
 * @author Denis Rylow
 */
public class ServletAuthFilter implements Filter {
    private FilterConfig filterConfig = null;
    private ServletAuthFilterService servletAuthFilterHelper = null;
    private String redirectLocation = null;
    private Map<String, String> headerNameOfTokenClaim = null;
    private Map<String, String> attributeNameOfTokenClaim = null;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        init(filterConfig, new ServletAuthFilterServiceImpl(filterConfig));
    }

    void init(FilterConfig filterConfig, ServletAuthFilterService servletAuthFilterHelper) {
        this.filterConfig = filterConfig;
        this.servletAuthFilterHelper = servletAuthFilterHelper;
        this.redirectLocation = filterConfig.getInitParameter(FilterConfigKeys.REDIRECT_LOCATION_KEY);
        this.headerNameOfTokenClaim = ParseStringUtils.parseConfigValueAsMap(
                filterConfig.getInitParameter(FilterConfigKeys.CLAIMS_HEADERS_MAP_KEY)
        );
        this.attributeNameOfTokenClaim = ParseStringUtils.parseConfigValueAsMap(
                filterConfig.getInitParameter(FilterConfigKeys.CLAIMS_ATTRIBUTES_MAP_KEY)
        );
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String accToken = servletAuthFilterHelper.extractAccessToken(request);
        if (accToken!=null) {
            Principal principal = servletAuthFilterHelper.authenticate(accToken);
            if (principal!=null) {
                request = new ServletAuthFilterHttpRequestWrapper(request, principal, headerNameOfTokenClaim, attributeNameOfTokenClaim);
                AuthFilterLogger.LOG.infoSuccessAuthentication(LoggerUtils.trimAccessTokenForLogging(accToken));
                filterChain.doFilter(request, response);
            } else {
                AuthFilterLogger.LOG.infoRedirectDueToBadToken(LoggerUtils.trimAccessTokenForLogging(accToken));
                response.sendRedirect(redirectLocation);
            }
        } else {
            AuthFilterLogger.LOG.infoRedirectDueToNoToken(request.getRemoteAddr());
            response.sendRedirect(redirectLocation);
        }
    }

    @Override
    public void destroy() {

    }
}
