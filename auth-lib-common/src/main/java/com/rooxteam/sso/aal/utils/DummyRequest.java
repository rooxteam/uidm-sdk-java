package com.rooxteam.sso.aal.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Enumeration;

public class DummyRequest extends HttpServletRequestWrapper {
    private static final HttpServletRequest UNSUPPORTED_REQUEST = (HttpServletRequest) Proxy.newProxyInstance(DummyRequest.class.getClassLoader(), new Class[]{HttpServletRequest.class}, new UnsupportedOperationExceptionInvocationHandler());
    private static final String LOCAL_HOST = "127.0.0.1";
    private static final String EMPTY = "";

    private static final DummyRequest instance = new DummyRequest();

    private DummyRequest() {
        super(UNSUPPORTED_REQUEST);
    }

    public static DummyRequest getInstance() {
        return instance;
    }

    @Override
    public String getRemoteAddr() {
        return LOCAL_HOST;
    }

    @Override
    public String getRequestURI() {
        return EMPTY;
    }

    @Override
    public String getMethod() {
        return EMPTY;
    }

    @Override
    public Enumeration getHeaderNames() {
        return Collections.enumeration(Collections.emptyList());
    }

    @Override
    public Enumeration getHeaders(final String name) {
        return Collections.enumeration(Collections.emptyList());
    }

    private final static class UnsupportedOperationExceptionInvocationHandler implements InvocationHandler {
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            throw new UnsupportedOperationException(method + " is not supported");
        }
    }
}