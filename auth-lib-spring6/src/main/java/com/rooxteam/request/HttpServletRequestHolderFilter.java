package com.rooxteam.request;

import com.rooxteam.sso.aal.request.HttpRequestInfoHolder;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

public class HttpServletRequestHolderFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            HttpRequestInfoHolder.setRequestInfo(new HttpRequestInfoImpl((HttpServletRequest) servletRequest));
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            HttpRequestInfoHolder.clear();
        }
    }

    @Override
    public void destroy() {

    }
}
