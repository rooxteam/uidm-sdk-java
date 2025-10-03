package com.rooxteam.uidm.sdk.client.cookies;

import com.rooxteam.sso.aal.client.cookies.RequestCookieStoreHolder;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;

/**
 * @author Ivan Volynkin
 *         ivolynkin@roox.ru
 */
public class RequestCookieStoreFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        RequestCookieStoreHolder.setCookieStoreThreadLocal();
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            RequestCookieStoreHolder.clearCookieStore();
        }
    }

    @Override
    public void destroy() {
    }
}