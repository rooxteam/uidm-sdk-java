package com.rooxteam.sso.aal.client.cookies;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author Ivan Volynkin
 *         ivolynkin@roox.ru
 */
public class RequestCookieStoreFilter implements Filter {

    private final com.rooxteam.sso.aal.client.cookies.CookieStoreFactory cookieStoreFactory;

    public RequestCookieStoreFilter(CookieStoreFactory cookieStoreFactory) {
        this.cookieStoreFactory = cookieStoreFactory;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        RequestCookieStoreHolder.setCookieStoreThreadLocal(cookieStoreFactory.createCookieStore());
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }
}