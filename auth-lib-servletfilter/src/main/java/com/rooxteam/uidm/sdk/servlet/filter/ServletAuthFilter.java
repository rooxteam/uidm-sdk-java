package com.rooxteam.uidm.sdk.servlet.filter;

import com.rooxteam.sso.aal.Principal;
import com.rooxteam.uidm.sdk.servlet.configuration.FilterConfigKeys;
import org.apache.http.HttpHeaders;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class ServletAuthFilter implements Filter {
    private FilterConfig filterConfig = null;
    private ServletFilterHelper servletFilterHelper = null;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        init(filterConfig, new ServletFilterHelper(filterConfig));
    }

    void init(FilterConfig filterConfig, ServletFilterHelper servletFilterHelper) {
        this.filterConfig = filterConfig;
        this.servletFilterHelper = servletFilterHelper;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        boolean redirect = false;
        Optional<String> accToken = servletFilterHelper.extractAccessToken(request.getCookies(), request.getHeader(HttpHeaders.AUTHORIZATION));
        if (accToken.isPresent()) {
            Optional<Principal> principal = servletFilterHelper.getPrincipal(accToken.get());
            if (principal.isPresent()) {
                request = new ServletFilterHttpRequestWrapper(request, principal.get(), filterConfig);
            } else {
                redirect = true;
            }
        } else {
            redirect = true;
        }

        if (redirect) {
            response.sendRedirect(filterConfig.getInitParameter(FilterConfigKeys.REDIRECT_LOCATION_KEY));
        } else {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
