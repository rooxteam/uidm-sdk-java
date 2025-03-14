package com.rooxteam.sso.aal.client.context;

import java.util.Collections;
import java.util.Map;

public class NoRequestClientContextProvider implements ClientContextProvider {
    public static final ClientContextProvider INSTANCE = new NoRequestClientContextProvider();
    private static final Map<String, Object> CONTEXT = Collections.singletonMap("ip", "127.0.0.1");

    @Override
    public Map<String, Object> getContext() {
        return CONTEXT;
    }
}
