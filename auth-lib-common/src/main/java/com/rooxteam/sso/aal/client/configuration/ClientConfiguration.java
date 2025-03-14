package com.rooxteam.sso.aal.client.configuration;

import com.rooxteam.sso.aal.ConnectionReuseStrategy;

import java.net.URI;

public interface ClientConfiguration {

    String getClientSecret(String clientId);

    URI getAccessTokenEndpoint();

    URI getTokenExchangeEndpoint();

    URI getTokenValidationEndpoint();

    String getUidmRealm();

    String getHeaderPrefix();

    int getConnectTimeout();

    int getReadTimeout();

    int getPoolSize();

    int getPoolSizePerRoute();

    ConnectionReuseStrategy getConnectionReuseStrategy();

    boolean isCookieStorePerRequestEnabled();

}
