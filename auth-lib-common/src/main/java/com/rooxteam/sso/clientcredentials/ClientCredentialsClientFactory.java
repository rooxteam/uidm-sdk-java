package com.rooxteam.sso.clientcredentials;

import com.rooxteam.compat.Objects;
import com.rooxteam.sso.aal.client.HttpClientFactory;
import com.rooxteam.sso.aal.client.MonitoringHttpClientRequestInterceptor;
import com.rooxteam.sso.aal.client.configuration.ClientConfiguration;
import com.rooxteam.sso.aal.client.configuration.ClientConfigurationFactory;
import com.rooxteam.sso.aal.utils.StringUtils;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.HttpRequestInterceptor;


@SuppressWarnings("unused")
public final class ClientCredentialsClientFactory {

    public static ClientCredentialsClient create(
            final com.rooxteam.sso.clientcredentials.configuration.Configuration config
    ) {
        CloseableHttpClient defaultClient = HttpClientFactory.createWithDefaultConfiguration();

        return create(config, defaultClient);
    }

    /**
     * Instantiate new instance of ClientCredentialsClient.
     * Use one instance of ClientCredentialsClient for each client_id.
     *
     * @param aalConfig     AAL configuration.
     * @param configuration Client configuration.
     * @return ClientCredentialsClient
     */
    public static ClientCredentialsClient create(
            final com.rooxteam.sso.aal.configuration.Configuration aalConfig,
            final com.rooxteam.sso.clientcredentials.configuration.Configuration configuration
    ) {
        ClientConfiguration clientConfiguration = ClientConfigurationFactory.create(aalConfig);
        HttpRequestInterceptor interceptor = new MonitoringHttpClientRequestInterceptor(aalConfig);
        CloseableHttpClient httpClient = HttpClientFactory.create(clientConfiguration, interceptor);

        return create(configuration, httpClient);
    }

    /**
     * Instantiate new instance of ClientCredentialsClient.
     * Use one instance of ClientCredentialsClient for each client_id.
     *
     * @param config     Client configuration.
     * @param httpClient HttpClient used for making requests. When using this signature it's caller responsibility
     *                   to configure it properly. Otherwise use ClientCredentialsClient create(final Configuration
     *                   config) signature
     * @return ClientCredentialsClient
     */
    @SuppressWarnings("WeakerAccess")
    public static ClientCredentialsClient create(
            final com.rooxteam.sso.clientcredentials.configuration.Configuration config,
            final CloseableHttpClient httpClient
    ) {
        Objects.requireNonNull(config, "config");
        Objects.requireNonNull(config, "httpClient");

        final ClientCredentialsClientBuilder builder = new ClientCredentialsClientBuilder(
                httpClient,
                config.getAccessTokenEndpoint(),
                config.getTokenValidationEndpoint(),
                config);

        if (!StringUtils.isEmpty(config.getHeaderPrefix())) {
            builder.headerPrefix(config.getHeaderPrefix());
        } else {
            builder.headerPrefix("Bearer ");
        }

        builder.params("client_id", config.getClientId());
        builder.params("client_secret", config.getClientSecret());
        builder.params("grant_type", "client_credentials");

        if (!StringUtils.isEmpty(config.getUidmRealm())) {
            builder.params("realm", config.getUidmRealm());
        }

        if (config.getAdditionalRequestParameters() != null) {
            builder.params(config.getAdditionalRequestParameters());
        }

        return builder.build();
    }
}
