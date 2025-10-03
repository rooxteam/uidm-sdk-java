package com.rooxteam.sso.aal.client.context;

import com.rooxteam.sso.aal.client.RequestContextCollector;
import com.rooxteam.sso.aal.request.HttpRequestInfo;
import com.rooxteam.sso.aal.request.HttpRequestInfoProvider;

import java.util.Map;

public class ClientContextProviderDefault implements ClientContextProvider {
    private final RequestContextCollector requestContextCollector;
    private final HttpRequestInfoProvider httpRequestInfoProvider;

    public ClientContextProviderDefault(RequestContextCollector requestContextCollector,
                                        HttpRequestInfoProvider httpRequestInfoProvider) {
        this.requestContextCollector = requestContextCollector;
        this.httpRequestInfoProvider = httpRequestInfoProvider;
    }

    @Override
    public Map<String, Object> getContext() {
        HttpRequestInfo request = httpRequestInfoProvider.getHttpRequestInfo();
        if (request != null) {
            return requestContextCollector.collect(request);
        }
        return NoRequestClientContextProvider.INSTANCE.getContext();
    }
}
