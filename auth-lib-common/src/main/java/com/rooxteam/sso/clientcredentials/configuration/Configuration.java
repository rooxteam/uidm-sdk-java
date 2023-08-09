package com.rooxteam.sso.clientcredentials.configuration;

import com.rooxteam.sso.aal.ProviderType;

import java.net.URI;
import java.util.Map;

public interface Configuration {

    String ACCESS_TOKEN_URL = "/oauth2/access_token";
    String TOKEN_INFO_URL = "/oauth2/tokeninfo";

    URI getAccessTokenEndpoint();

    URI getTokenValidationEndpoint();

    String getClientId();

    String getClientSecret();

    String getUidmRealm();

    Map<String,String> getAdditionalRequestParameters();

    String getHeaderPrefix();

    int getConnectTimeout();

    int getReadTimeout();

    int getPoolSize();

    boolean isTokensCacheEnabled();

    int getUpdateTimeBeforeTokenExpiration();

    boolean legacyMaskingEnabled();

    ProviderType getProviderType();
}
