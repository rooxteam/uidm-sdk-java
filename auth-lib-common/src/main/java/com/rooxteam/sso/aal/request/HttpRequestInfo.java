package com.rooxteam.sso.aal.request;

import org.apache.hc.core5.http.MessageHeaders;

import java.util.List;
import java.util.Map;

public interface HttpRequestInfo {

    Map<String, List<String>> getHeaders();

    String getHeaderFirstValue(String headerName);

    String getRemoteAddr();

    String getRequestURI();

    String getMethod();

    String getBody();

    String getServerName();

    String getQueryString();

    String getContentType();

    String getContextPath();

    String getServletPath();

    String getPathInfo();

}
