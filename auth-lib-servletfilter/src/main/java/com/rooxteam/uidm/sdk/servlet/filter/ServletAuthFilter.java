package com.rooxteam.uidm.sdk.servlet.filter;

import com.rooxteam.sso.aal.Principal;
import com.rooxteam.uidm.sdk.servlet.AuthFilterLogger;
import com.rooxteam.uidm.sdk.servlet.configuration.FilterConfigKeys;
import com.rooxteam.uidm.sdk.servlet.util.StringUtils;

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
import java.util.Optional;

/**
 * This filter attempts to authenticate the user by Cookie or, if absent, by Authorization header.
 * If the user can not be authenticated, then the user is redirected to authentication endpoint.
 * On successful authentication some claims from access token are placed in the request and forwarded.
 * @author Denis Rylow
 */
public class ServletAuthFilter implements Filter {
    private FilterConfig filterConfig = null;
    private ServletAuthFilterHelper servletAuthFilterHelper = null;
    private String redirectLocation = null;
    private Map<String, String> claimHead = null;
    private Map<String, String> claimAttribute = null;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        init(filterConfig, new ServletAuthFilterHelper(filterConfig));
    }

    void init(FilterConfig filterConfig, ServletAuthFilterHelper servletAuthFilterHelper) {
        this.filterConfig = filterConfig;
        this.servletAuthFilterHelper = servletAuthFilterHelper;
        this.redirectLocation = filterConfig.getInitParameter(FilterConfigKeys.REDIRECT_LOCATION_KEY);
        this.claimHead = StringUtils.parseConfigValueAsMap(
                filterConfig.getInitParameter(FilterConfigKeys.CLAIMS_HEADERS_MAP_KEY)
        );
        this.claimAttribute = StringUtils.parseConfigValueAsMap(
                filterConfig.getInitParameter(FilterConfigKeys.CLAIMS_ATTRIBUTES_MAP_KEY)
        );
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        Optional<String> accToken = servletAuthFilterHelper.extractAccessToken(request);
        if (accToken.isPresent()) {
            Optional<Principal> principal = servletAuthFilterHelper.authenticate(accToken.get());
            if (principal.isPresent()) {
                request = new ServletAuthFilterHttpRequestWrapper(request, principal.get(), claimHead, claimAttribute);
                AuthFilterLogger.LOG.infoSuccessAuthentication(servletAuthFilterHelper.trimAccessTokenForLogging(accToken.get()));
                filterChain.doFilter(request, response);
            } else {
                AuthFilterLogger.LOG.infoRedirectDueToBadToken(servletAuthFilterHelper.trimAccessTokenForLogging(accToken.get()));
                response.sendRedirect(redirectLocation);
            }
        } else {
            response.sendRedirect(redirectLocation);
        }
    }

    @Override
    public void destroy() {

    }
}
