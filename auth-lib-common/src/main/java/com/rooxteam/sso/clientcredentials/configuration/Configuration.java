package com.rooxteam.sso.clientcredentials.configuration;

import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

public interface Configuration {

    final String ACCESS_TOKEN_URL = "/oauth2/access_token";
    final String TOKEN_INFO_URL = "/oauth2/tokeninfo";

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


}
