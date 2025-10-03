package com.rooxteam.request;

import com.rooxteam.sso.aal.request.HttpRequestInfo;
import com.rooxteam.sso.aal.request.HttpRequestInfoProvider;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Spring implementation of {@see com.rooxteam.sso.aal.request.HttpRequestInfoProvider}
 * Retrieves request from {@see org.springframework.web.context.request.RequestContextHolder}
 */
public class HttpRequestInfoProviderSpringImpl implements HttpRequestInfoProvider {

    @Override
    public HttpRequestInfo getHttpRequestInfo() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return new HttpRequestInfoImpl(((ServletRequestAttributes) requestAttributes).getRequest());
        }
        return null;
    }
}
