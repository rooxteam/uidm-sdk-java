package com.rooxteam.sso.aal.client;

import com.rooxteam.sso.aal.configuration.Configuration;
import lombok.Setter;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;
import org.jboss.logging.MDC;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dmitry Tikhonov
 */
public class MonitoringHttpClientRequestInterceptor implements HttpRequestInterceptor {
    private final Map<String, String> forwardingHeaderMap = new HashMap<String, String>() {{
        put("session_id", "session_id");
        put("system_id", "system-id");
        put("context_id", "context-id");
        put("user_id", "user-id");
    }};

    @Setter
    private Configuration config;

    public MonitoringHttpClientRequestInterceptor(Configuration config) {
        this.config = config;
    }

    @Override
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        Map<String, String> forwardingHeaders = getForwardingHeaders();
        for (Map.Entry<String, String> header : forwardingHeaders.entrySet()) {
            request.setHeader(header.getKey(), header.getValue());
        }
    }

    private Map<String, String> getForwardingHeaders() {
        Map<String, String> headers = new HashMap<String, String>();

        for (Map.Entry<String, String> entry : forwardingHeaderMap.entrySet()) {
            Object value = MDC.get(entry.getValue());
            if (value != null) {
                String headerName = config.getString("com.rooxteam.webapi.filters.logFilter.headers." + entry.getKey());
                if (headerName != null) {
                    headers.put(headerName, value.toString());
                }
            }
        }

        for (Object o : config.getList("com.rooxteam.webapi.filters.logFilter.headers.forwarding.list")) {
            if (o != null) {
                String key = o.toString();
                Object value = MDC.get(key);
                if (value != null) {
                    headers.put(key, value.toString());
                }
            }
        }

        return headers;
    }
}
