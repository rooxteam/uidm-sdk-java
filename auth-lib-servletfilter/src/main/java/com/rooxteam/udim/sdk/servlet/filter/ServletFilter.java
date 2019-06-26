package com.rooxteam.udim.sdk.servlet.filter;

import com.rooxteam.udim.sdk.servlet.configuration.Configuration;
import com.rooxteam.udim.sdk.servlet.dto.TokenInfo;
import com.rooxteam.udim.sdk.servlet.service.ValidateTokenService;
import com.rooxteam.udim.sdk.servlet.service.ValidateTokenServiceImpl;
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

public class ServletFilter implements Filter {
    private final ValidateTokenService validateTokenService;
    private final Configuration config;

    public ServletFilter(Configuration configuration) {
        this.validateTokenService = new ValidateTokenServiceImpl(
                configuration.getSocketTimeout(),
                configuration.getConnectionTimeout(),
                configuration.getConnectionRequestTimeout()
        );
        this.config = configuration;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        boolean redirect = false;
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        Optional<String> accToken = validateTokenService.extractAccessToken(authHeader);
        if (accToken.isPresent()) {
            Optional<TokenInfo> info = validateTokenService.getAccessTokenInfo(config.getTokenInfoUrl(), accToken.get());
            if (info.isPresent()) {
                request = new ServletFilterHttpRequestWrapper(request, info.get(), config);
            } else {
                redirect = true;
            }
        } else {
            redirect = true;
        }

        if (redirect) {
            response.sendRedirect(config.getRedirectLocation());
        }

        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
