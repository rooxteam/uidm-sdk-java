package com.rooxteam.udim.sdk.servlet.filter;

import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.client.CommonSsoAuthorizationClient;
import com.rooxteam.sso.aal.client.SsoAuthorizationClientByConfig;
import com.rooxteam.sso.aal.client.SsoAuthorizationClientByJwt;
import com.rooxteam.udim.sdk.servlet.configuration.ConfigValues;
import com.rooxteam.udim.sdk.servlet.configuration.ServletFilterConfiguration;
import com.rooxteam.udim.sdk.servlet.exceptions.AlreadyInitializedException;
import com.rooxteam.udim.sdk.servlet.exceptions.NotInitializedException;
import com.rooxteam.udim.sdk.servlet.service.ServletFilterService;
import com.rooxteam.udim.sdk.servlet.service.ServletFilterServiceImpl;
import org.apache.http.HttpHeaders;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

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
    private ServletFilterService servletFilterService;
    private ServletFilterConfiguration config;
    private boolean fullyInitialized = false;

    private void assureNotInitialised() {
        if (fullyInitialized) {
            throw new AlreadyInitializedException();
        }
    }

    private void assureInitialised() {
        if (!fullyInitialized) {
            throw new NotInitializedException();
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //do nothing
    }

    /**
     * Use finishInit() methods to finish initialization.
     */
    public void finishInit(ServletFilterConfiguration servletFilterConfiguration) {
        CloseableHttpClient closeableHttpClient = HttpClientBuilder
                .create()
                .build();
        SsoAuthorizationClientByJwt client = new SsoAuthorizationClientByJwt(new AalConfigAdapter(servletFilterConfiguration), closeableHttpClient);
        finishInit(client, servletFilterConfiguration);
    }

    public void finishInit(CommonSsoAuthorizationClient client, ServletFilterConfiguration servletFilterConfiguration) {
        assureNotInitialised();
        this.servletFilterService = new ServletFilterServiceImpl(client, servletFilterConfiguration);
        this.config = servletFilterConfiguration;
        this.fullyInitialized = true;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        assureInitialised();
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        boolean redirect = false;
        Optional<String> accToken = servletFilterService.extractAccessToken(request.getCookies(), request.getHeader(HttpHeaders.AUTHORIZATION));
        if (accToken.isPresent()) {
            Optional<Principal> info = servletFilterService.getPrincipal(request, accToken.get());
            if (info.isPresent()) {
                request = new ServletFilterHttpRequestWrapper(request, info.get(), config);
            } else {
                redirect = true;
            }
        } else {
            redirect = true;
        }

        if (redirect) {
            response.sendRedirect(config.getString(ConfigValues.REDIRECT_LOCATION_KEY));
        }

        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
