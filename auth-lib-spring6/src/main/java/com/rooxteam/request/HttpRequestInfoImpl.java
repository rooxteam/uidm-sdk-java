package com.rooxteam.request;

import com.rooxteam.sso.aal.request.HttpRequestInfo;
import lombok.experimental.Delegate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.ContentCachingRequestWrapper;

import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;


public class HttpRequestInfoImpl implements HttpRequestInfo {
    @Delegate(types = HttpRequestInfo.class, excludes = Exclude.class)
    private final HttpServletRequest request;

    public HttpRequestInfoImpl(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        return headersMap(request);
    }

    public String getHeaderFirstValue(String headerName) {
        return request.getHeader(headerName);
    }

    public String getBody() {
        if (!(request instanceof ContentCachingRequestWrapper)) {
            return "";
        }
        ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) request;
        byte[] requestData = requestWrapper.getContentAsByteArray();
        if (requestData.length == 0) {
            return "";
        }
        String encoding = Optional.of(requestWrapper.getCharacterEncoding())
                .orElse(StandardCharsets.UTF_8.displayName());
        try {
            return new String(requestData, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unknown request encoding '" + encoding + "'", e);
        }
    }

    private static MultiValueMap<String, String> headersMap(HttpServletRequest request) {
        MultiValueMap<String, String> headers =
                CollectionUtils.toMultiValueMap(new LinkedCaseInsensitiveMap<>(8, Locale.ENGLISH));
        for (Enumeration<?> names = request.getHeaderNames(); names.hasMoreElements(); ) {
            String name = (String) names.nextElement();
            for (Enumeration<?> values = request.getHeaders(name); values.hasMoreElements(); ) {
                headers.add(name, (String) values.nextElement());
            }
        }
        return headers;
    }

    private interface Exclude {
        String getBody();

        String getHeaderFirstValue(String headerName);

        Map<String, List<String>> getHeaders();
    }
}
