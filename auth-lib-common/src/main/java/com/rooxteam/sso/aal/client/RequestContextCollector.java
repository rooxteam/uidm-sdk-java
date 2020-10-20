package com.rooxteam.sso.aal.client;

import com.google.common.collect.ImmutableMap;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ivan Volynkin
 * ivolynkin@roox.ru
 */
@Component
public class RequestContextCollector {

    public Map<String, Object> collect(HttpServletRequest request) {
        if (request != null) {
            return ImmutableMap.of(
                    "headers", getRequestHeaders(request),
                    "url", request.getRequestURI(),
                    "httpMethod", request.getMethod(),
                    "ip", request.getRemoteAddr()
            );
        } else {
            return Collections.emptyMap();
        }
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