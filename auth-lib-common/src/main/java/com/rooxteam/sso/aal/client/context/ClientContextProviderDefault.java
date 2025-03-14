package com.rooxteam.sso.aal.client.context;

import com.rooxteam.sso.aal.client.RequestContextCollector;
import com.rooxteam.sso.aal.request.HttpServletRequestProvider;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class ClientContextProviderDefault implements ClientContextProvider {
    private final RequestContextCollector requestContextCollector;
    private final HttpServletRequestProvider httpServletRequestProvider;

    public ClientContextProviderDefault(RequestContextCollector requestContextCollector,
                                        HttpServletRequestProvider httpServletRequestProvider) {
        this.requestContextCollector = requestContextCollector;
        this.httpServletRequestProvider = httpServletRequestProvider;
    }

    @Override
    public Map<String, Object> getContext() {
        HttpServletRequest request = httpServletRequestProvider.getHttpServletRequest();
        if (request != null) {
            return requestContextCollector.collect(request);
        }
        return NoRequestClientContextProvider.INSTANCE.getContext();
    }
}
