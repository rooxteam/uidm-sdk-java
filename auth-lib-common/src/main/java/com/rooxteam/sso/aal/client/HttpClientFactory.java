package com.rooxteam.sso.aal.client;

import com.rooxteam.sso.aal.ConnectionReuseStrategy;
import com.rooxteam.sso.aal.client.configuration.ClientConfiguration;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.impl.DefaultConnectionReuseStrategy;
import org.apache.hc.core5.util.Timeout;

import java.util.Arrays;

public class HttpClientFactory {

    public static CloseableHttpClient create(ClientConfiguration config, HttpRequestInterceptor... interceptors) {
        PoolingHttpClientConnectionManager connectionManager = createConnectionManager(config);
        org.apache.hc.core5.http.ConnectionReuseStrategy reuseStrategy = createReuseStrategy(config);
        RequestConfig requestConfig = createRequestConfig(config);

        return createHttpClient(reuseStrategy, requestConfig, connectionManager, interceptors);
    }

    public static CloseableHttpClient createWithDefaultConfiguration() {
        return HttpClients.createDefault();
    }

    private static PoolingHttpClientConnectionManager createConnectionManager(ClientConfiguration config) {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(config.getPoolSize());
        connectionManager.setDefaultMaxPerRoute(config.getPoolSizePerRoute());

        return connectionManager;
    }

    private static RequestConfig createRequestConfig(ClientConfiguration config) {
        return RequestConfig.custom()
                .setResponseTimeout((Timeout.ofMilliseconds(config.getReadTimeout())))
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(config.getConnectTimeout()))
                .build();
    }

    private static org.apache.hc.core5.http.ConnectionReuseStrategy createReuseStrategy(ClientConfiguration config) {
        ConnectionReuseStrategy connectionReuseStrategy = config.getConnectionReuseStrategy();
        if (connectionReuseStrategy == ConnectionReuseStrategy.NO_REUSE) {
            return new NoConnectionReuseStrategy();
        }

        if (connectionReuseStrategy == ConnectionReuseStrategy.KEEP_ALIVE) {
            return new DefaultConnectionReuseStrategy();
        }
        throw new IllegalArgumentException("Unexpected reuse strategy: " + connectionReuseStrategy);
    }

    private static CloseableHttpClient createHttpClient(org.apache.hc.core5.http.ConnectionReuseStrategy reuseStrategy,
                                                        RequestConfig requestConfig,
                                                        PoolingHttpClientConnectionManager connectionManager,
                                                        HttpRequestInterceptor... interceptors) {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .disableCookieManagement()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionReuseStrategy(reuseStrategy);
        if (interceptors != null) {
            Arrays.stream(interceptors).forEach(httpClientBuilder::addRequestInterceptorLast);
        }
        return httpClientBuilder.build();
    }

}
