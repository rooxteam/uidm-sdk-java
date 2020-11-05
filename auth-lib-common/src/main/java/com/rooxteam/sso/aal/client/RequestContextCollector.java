package com.rooxteam.sso.aal.client;

import com.rooxteam.sso.aal.configuration.Configuration;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rooxteam.sso.aal.ConfigKeys.USER_CONTEXT_IP_HEADER;
import static com.rooxteam.sso.aal.ConfigKeys.USER_CONTEXT_IP_HEADER_DEFAULT;
import static com.rooxteam.sso.aal.ConfigKeys.USER_CONTEXT_IP_SOURCE;

/**
 * @author Ivan Volynkin
 * ivolynkin@roox.ru
 */
@Component
public class RequestContextCollector {

    private final Configuration config;

    public RequestContextCollector(Configuration config) {
        this.config = config;
    }

    public Map<String, Object> collect(HttpServletRequest request) {
        if (request != null) {
            Map<String, Object> contextMap = new HashMap<String, Object>();
            contextMap.put("headers", getRequestHeaders(request));
            contextMap.put("url", request.getRequestURI());
            contextMap.put("httpMethod", request.getMethod());
            String ip = getIpFromRequest(request);
            if (ip != null && !ip.isEmpty()) {
                contextMap.put("ip", ip);
            }
            return Collections.unmodifiableMap(contextMap);
        } else {
            return Collections.emptyMap();
        }
    }

    private String getIpFromRequest(HttpServletRequest request) {
        String ipSource = config.getString(USER_CONTEXT_IP_SOURCE);
        if ("request".equalsIgnoreCase(ipSource)) {
            return request.getRemoteAddr();
        } else if ("header".equalsIgnoreCase(ipSource)) {
            String headerName = config.getString(USER_CONTEXT_IP_HEADER, USER_CONTEXT_IP_HEADER_DEFAULT);
            return request.getHeader(headerName);
        }
        return null;
    }

    @SneakyThrows
    private Map<String, List<String>> getRequestHeaders(HttpServletRequest request) {
        Map<String, List<String>> headers = new HashMap<String, List<String>>();

        Enumeration names;
        if (request == null) {
            return Collections.emptyMap();
        }
        if (request.getHeaderNames() != null) {
            names = request.getHeaderNames();
        } else {
            names = Collections.enumeration(Collections.emptyList());
        }
        while (names.hasMoreElements()) {
            ArrayList<String> list = new ArrayList<String>();
            String name = (String) names.nextElement();
            Enumeration values = request.getHeaders(name);
            if (values == null) {
                values = Collections.enumeration(Collections.emptyList());
            }
            while (values.hasMoreElements()) {
                String value = (String) values.nextElement();
                list.add(value);
            }
            headers.put(name, list);
        }
        return headers;
    }

}