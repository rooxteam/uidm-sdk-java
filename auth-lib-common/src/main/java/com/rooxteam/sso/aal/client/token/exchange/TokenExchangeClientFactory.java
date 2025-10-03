package com.rooxteam.sso.aal.client.token.exchange;

import com.rooxteam.sso.aal.client.HttpClientFactory;
import com.rooxteam.sso.aal.client.MonitoringHttpClientRequestInterceptor;
import com.rooxteam.sso.aal.client.configuration.ClientConfiguration;
import com.rooxteam.sso.aal.client.configuration.ClientConfigurationFactory;
import com.rooxteam.sso.aal.configuration.Configuration;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.HttpRequestInterceptor;

@SuppressWarnings("unused")
public class TokenExchangeClientFactory {

    /**
     * Instantiate new instance of TokenExchangeClient.
     *
     * @param config configuration. Use Configuration Factory for instantiation from different sources
     * @return TokenExchangeClient
     */
    public static TokenExchangeClient create(Configuration config) {
        ClientConfiguration clientConfiguration = ClientConfigurationFactory.create(config);
        HttpRequestInterceptor interceptor = new MonitoringHttpClientRequestInterceptor(config);
        CloseableHttpClient httpClient = HttpClientFactory.create(clientConfiguration, interceptor);
        return create(config, httpClient, true);
    }

    /**
     * Instantiate new instance of TokenExchangeClient.
     *
     * @param config     configuration. Use Configuration Factory for instantiation from different sources
     * @param httpClient HttpClient used for requests. When using this signature it's caller responsibility
     *                   to configure it properly. Otherwise use TokenExchangeClient create(final Configuration
     *                   config) signature
     * @return TokenExchangeClient
     */
    public static TokenExchangeClient create(Configuration config, CloseableHttpClient httpClient) {
        return create(config, httpClient, false);
    }

    private static TokenExchangeClient create(Configuration configuration,
                                              CloseableHttpClient closableHttpClient,
                                              boolean shouldCloseHttpClient) {
        ClientConfiguration clientConfiguration = ClientConfigurationFactory.create(configuration);
        return new TokenExchangeClientImpl(clientConfiguration, closableHttpClient, shouldCloseHttpClient);
    }
}
