package com.rooxteam.sso.aal.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rooxteam.sso.aal.client.configuration.ClientConfiguration;
import com.rooxteam.sso.aal.client.configuration.ClientConfigurationFactory;
import com.rooxteam.sso.aal.configuration.Configuration;
import com.rooxteam.sso.aal.realms.RealmDetermineService;
import com.rooxteam.sso.aal.realms.impl.RealmDetermineServiceImpl;
import com.rooxteam.sso.aal.request.HttpRequestInfoProvider;
import com.rooxteam.sso.aal.request.HttpRequestInfoProviderDefault;
import com.rooxteam.sso.aal.userIp.UserIpProviderFactory;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

/**
 * @author Ivan Volynkin
 * ivolynkin@roox.ru
 */
abstract public class CommonSsoAuthorizationClient implements SsoAuthorizationClient {
    protected final Configuration config;
    protected final ClientConfiguration clientConfiguration;
    protected final CloseableHttpClient httpClient;
    protected final RequestContextCollector requestContextCollector;
    protected final RealmDetermineService realmDetermineService;
    protected final HttpRequestInfoProvider httpRequestInfoProvider;

    protected CommonSsoAuthorizationClient(Configuration config, CloseableHttpClient httpClient) {
        this(config, httpClient, new RequestContextCollector(new UserIpProviderFactory(config).create()),
                RealmDetermineServiceImpl.builder().defaultRealmFromConfiguration(config).build());
    }

    protected CommonSsoAuthorizationClient(Configuration config, CloseableHttpClient httpClient, RealmDetermineService realmDetermineService) {
        this(config, httpClient, new RequestContextCollector(new UserIpProviderFactory(config).create()), realmDetermineService);
    }

    protected CommonSsoAuthorizationClient(Configuration config, CloseableHttpClient httpClient,
                                           RequestContextCollector requestContextCollector,
                                           RealmDetermineService realmDetermineService) {
        this.config = config;
        this.httpClient = httpClient;
        this.requestContextCollector = requestContextCollector;
        this.realmDetermineService = realmDetermineService;
        this.clientConfiguration = ClientConfigurationFactory.create(config);
        this.httpRequestInfoProvider = new HttpRequestInfoProviderDefault();
    }


}
