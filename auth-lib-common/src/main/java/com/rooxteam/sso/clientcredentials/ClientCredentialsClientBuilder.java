package com.rooxteam.sso.clientcredentials;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

/**
 * Used for client creation internally by this library
 */
@SuppressWarnings("unused")
final class ClientCredentialsClientBuilder {
    private final URI accessTokenEndpoint;
    private final URI tokenValidationEndpoint;
    private final RestTemplate restTemplate;
    private MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    private String headerPrefix = "";

    ClientCredentialsClientBuilder(final RestTemplate restTemplate, final URI accessTokenEndpoint, final URI tokenValidationEndpoint) {
        this.accessTokenEndpoint = accessTokenEndpoint;
        this.tokenValidationEndpoint = tokenValidationEndpoint;
        this.restTemplate = restTemplate;
    }

    ClientCredentialsClientBuilder params(String key, String value) {
        params.add(key, value);
        return this;
    }

    ClientCredentialsClientBuilder headerPrefix(String headerPrefix) {
        this.headerPrefix = headerPrefix;
        return this;
    }

    ClientCredentialsClientImpl build() {
        return new ClientCredentialsClientImpl(restTemplate, accessTokenEndpoint, tokenValidationEndpoint, params, headerPrefix);
    }

    ClientCredentialsClientBuilder params(final Map<String, String> additionalRequestParameters) {
        additionalRequestParameters.forEach((k, v) -> {
            this.params.add(k, v);
        });
        return this;
    }
}
