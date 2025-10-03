package com.rooxteam.sso.aal.client;

import com.rooxteam.sso.aal.request.HttpRequestInfo;
import com.rooxteam.sso.aal.userIp.UserIpProvider;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ivan Volynkin
 * ivolynkin@roox.ru
 */
public class RequestContextCollector {

    private final UserIpProvider userIpProvider;

    public RequestContextCollector(UserIpProvider userIpProvider) {
        this.userIpProvider = userIpProvider;
    }

    public Map<String, Object> collect(HttpRequestInfo request) {
        if (request != null) {
            Map<String, Object> contextMap = new HashMap<String, Object>();
            contextMap.put("headers", request.getHeaders());
            contextMap.put("url", request.getRequestURI());
            contextMap.put("httpMethod", request.getMethod());
            String ip = userIpProvider.getIpFromRequest(request);
            if (ip != null) {
                contextMap.put("ip", ip);
            }
            return Collections.unmodifiableMap(contextMap);
        } else {
            return Collections.emptyMap();
        }
    }
}