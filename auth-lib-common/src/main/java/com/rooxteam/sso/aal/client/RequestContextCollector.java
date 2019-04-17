package com.rooxteam.sso.aal.client;

import com.google.common.collect.ImmutableMap;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author Ivan Volynkin
 *         ivolynkin@roox.ru
 */
@Component
public class RequestContextCollector {

    public Map<String, Object> collect(HttpServletRequest request) {
        return ImmutableMap.of(
                "headers", getRequestHeaders(request),
                "url", request.getRequestURI(),
                "httpMethod", request.getMethod(),
                "ip", request.getRemoteAddr()
        );
    }

    @SneakyThrows
    private Map<String, List<String>> getRequestHeaders(HttpServletRequest request) {
        Map<String, List<String>> headers = new HashMap<>();
        Enumeration<String> names = Optional.ofNullable(request.getHeaderNames()).orElse(Collections.enumeration(Collections.emptyList()));
        while (names.hasMoreElements()) {
            ArrayList<String> list = new ArrayList<>();
            String name = names.nextElement();
            Enumeration<String> values = Optional.ofNullable(request.getHeaders(name)).orElse(Collections.enumeration(Collections.emptyList()));
            while (values.hasMoreElements()) {
                String value = values.nextElement();
                list.add(value);
            }
            headers.put(name, list);
        }
        return headers;
    }

}