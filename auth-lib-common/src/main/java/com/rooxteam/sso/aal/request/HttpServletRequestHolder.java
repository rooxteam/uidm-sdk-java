package com.rooxteam.sso.aal.request;

import javax.servlet.http.HttpServletRequest;

public class HttpServletRequestHolder {
    private static final ThreadLocal<HttpServletRequest> THREAD_LOCAL = new ThreadLocal<>();

    public static void setRequest(HttpServletRequest request) {
        THREAD_LOCAL.set(request);
    }

    public static HttpServletRequest getRequest() {
        return THREAD_LOCAL.get();
    }

    public static void clear() {
        THREAD_LOCAL.remove();
    }
}
