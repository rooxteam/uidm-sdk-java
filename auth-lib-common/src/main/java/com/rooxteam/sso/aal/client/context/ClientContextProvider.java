package com.rooxteam.sso.aal.client.context;

import java.util.Map;

public interface ClientContextProvider {

    Map<String, Object> getContext();

}
