package com.rooxteam.sso.clientcredentials;

import com.rooxteam.sso.clientcredentials.configuration.Configuration;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Used for client creation internally by this library
 */
@SuppressWarnings("unused")
final class ClientCredentialsClientBuilder {
    private final URI accessTokenEndpoint;
    private final URI tokenValidationEndpoint;
    private final CloseableHttpClient httpClient;
    private final Configuration configuration;
    private final Map<String, List<String>> params = new LinkedHashMap<>();
    private String headerPrefix = "";

    ClientCredentialsClientBuilder(final CloseableHttpClient httpClient,
                                   final URI accessTokenEndpoint,
                                   final URI tokenValidationEndpoint,
                                   final Configuration configuration) {
        this.accessTokenEndpoint = accessTokenEndpoint;
        this.tokenValidationEndpoint = tokenValidationEndpoint;
        this.httpClient = httpClient;
        this.configuration = configuration;
    }

    ClientCredentialsClientBuilder params(String key, String value) {
        add(key, value);
        return this;
    }

    ClientCredentialsClientBuilder headerPrefix(String headerPrefix) {
        this.headerPrefix = headerPrefix;
        return this;
    }

    ClientCredentialsClientImpl build() {
        return new ClientCredentialsClientImpl(httpClient,
                accessTokenEndpoint,
                tokenValidationEndpoint,
                params,
                headerPrefix,
                configuration);
    }

    ClientCredentialsClientBuilder params(final Map<String, String> additionalRequestParameters) {
        for (Map.Entry<String, String> entry : additionalRequestParameters.entrySet()) {
           add(entry.getKey(), entry.getValue());
        }
        return this;
    }

    private void add(String key, String value) {
        List<String> values = this.params.computeIfAbsent(key, k -> new LinkedList<>());

        values.add(value);
    }
}
