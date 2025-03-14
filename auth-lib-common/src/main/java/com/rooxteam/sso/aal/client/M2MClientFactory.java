package com.rooxteam.sso.aal.client;

import com.rooxteam.sso.aal.client.configuration.ClientConfiguration;
import com.rooxteam.sso.aal.client.configuration.ClientConfigurationFactory;
import com.rooxteam.sso.aal.client.context.ClientContextProvider;
import com.rooxteam.sso.aal.client.context.NoRequestClientContextProvider;
import com.rooxteam.sso.aal.configuration.Configuration;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.HttpRequestInterceptor;

@SuppressWarnings("unused")
public final class M2MClientFactory {

    public static M2MClient create(Configuration configuration) {
        ClientConfiguration clientConfiguration = ClientConfigurationFactory.create(configuration);
        HttpRequestInterceptor interceptor = new MonitoringHttpClientRequestInterceptor(configuration);
        CloseableHttpClient httpClient = HttpClientFactory.create(clientConfiguration, interceptor);
        return create(configuration, httpClient, NoRequestClientContextProvider.INSTANCE, true);
    }

    public static M2MClient create(Configuration configuration, CloseableHttpClient closableHttpClient) {
        return create(configuration, closableHttpClient, NoRequestClientContextProvider.INSTANCE, false);
    }

    public static M2MClient create(Configuration configuration,
                                   CloseableHttpClient closableHttpClient,
                                   ClientContextProvider clientContextProvider) {
        ClientConfiguration clientConfiguration = ClientConfigurationFactory.create(configuration);
        return new M2MClientImpl(clientConfiguration, closableHttpClient, clientContextProvider, false);
    }

    private static M2MClient create(Configuration configuration,
                                    CloseableHttpClient closableHttpClient,
                                    ClientContextProvider clientContextProvider,
                                    boolean shouldCloseHttpClient) {
        ClientConfiguration clientConfiguration = ClientConfigurationFactory.create(configuration);
        return new M2MClientImpl(clientConfiguration, closableHttpClient, clientContextProvider, shouldCloseHttpClient);
    }
}
