package com.rooxteam.sso.aal.request;

public class HttpRequestInfoHolder {
    private static final ThreadLocal<HttpRequestInfo> THREAD_LOCAL = new ThreadLocal<>();

    public static void setRequestInfo(HttpRequestInfo requestInfo) {
        THREAD_LOCAL.set(requestInfo);
    }

    public static HttpRequestInfo getRequestInfo() {
        return THREAD_LOCAL.get();
    }

    public static void clear() {
        THREAD_LOCAL.remove();
    }
}
