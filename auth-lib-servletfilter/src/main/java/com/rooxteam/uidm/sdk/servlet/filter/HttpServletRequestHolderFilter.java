package com.rooxteam.uidm.sdk.servlet.filter;

import com.rooxteam.sso.aal.request.HttpServletRequestHolder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class HttpServletRequestHolderFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            HttpServletRequestHolder.setRequest((HttpServletRequest) servletRequest);
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            HttpServletRequestHolder.clear();
        }
    }

    @Override
    public void destroy() {

    }
}
