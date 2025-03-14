package com.rooxteam.sso.aal;

import com.rooxteam.compat.Objects;
import com.rooxteam.sso.aal.client.MonitoringHttpClientRequestInterceptor;
import com.rooxteam.sso.aal.client.NoConnectionReuseStrategy;
import com.rooxteam.sso.aal.client.OtpClient;
import com.rooxteam.sso.aal.client.RequestContextCollector;
import com.rooxteam.sso.aal.client.SsoAuthenticationClient;
import com.rooxteam.sso.aal.client.SsoAuthorizationClient;
import com.rooxteam.sso.aal.client.SsoTokenClient;
import com.rooxteam.sso.aal.client.cookies.RequestCookieStore;
import com.rooxteam.sso.aal.configuration.Configuration;
import com.rooxteam.sso.aal.metrics.MetricsIntegration;
import com.rooxteam.sso.aal.metrics.MicrometerMetricsIntegration;
import com.rooxteam.sso.aal.metrics.NoOpMetricsIntegration;
import com.rooxteam.sso.aal.userIp.UserIpProvider;
import com.rooxteam.sso.aal.userIp.UserIpProviderFactory;
import com.rooxteam.sso.aal.validation.AccessTokenValidator;
import com.rooxteam.sso.aal.validation.claims.impl.TokenClaimValidatorImpl;
import com.rooxteam.sso.aal.validation.impl.JwtTokenValidator;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.ConnectionReuseStrategy;
import org.apache.hc.core5.http.impl.DefaultConnectionReuseStrategy;
import org.apache.hc.core5.util.Timeout;

import java.util.Timer;

import static com.rooxteam.sso.aal.ConfigKeys.AAL_VALIDATION_TYPE;
import static com.rooxteam.sso.aal.ConfigKeys.AUTHORIZATION_TYPE;
import static com.rooxteam.sso.aal.ConfigKeys.AUTHORIZATION_TYPE_DEFAULT;
import static com.rooxteam.sso.aal.ConfigKeys.CONNECTION_REUSE_STRATEGY;
import static com.rooxteam.sso.aal.ConfigKeys.CONNECTION_REUSE_STRATEGY_DEFAULT;
import static com.rooxteam.sso.aal.ConfigKeys.HTTP_CONNECTION_POOL_SIZE;
import static com.rooxteam.sso.aal.ConfigKeys.HTTP_CONNECTION_POOL_SIZE_DEFAULT;
import static com.rooxteam.sso.aal.ConfigKeys.HTTP_CONNECTION_POOL_SIZE_PER_ROUTE;
import static com.rooxteam.sso.aal.ConfigKeys.HTTP_CONNECTION_POOL_SIZE_PER_ROUTE_DEFAULT;
import static com.rooxteam.sso.aal.ConfigKeys.HTTP_CONNECTION_TIMEOUT;
import static com.rooxteam.sso.aal.ConfigKeys.HTTP_CONNECTION_TIMEOUT_DEFAULT;
import static com.rooxteam.sso.aal.ConfigKeys.HTTP_SOCKET_TIMEOUT;
import static com.rooxteam.sso.aal.ConfigKeys.HTTP_SOCKET_TIMEOUT_DEFAULT;
import static com.rooxteam.sso.aal.ConfigKeys.PRINCIPAL_PROVIDER_TYPE;
import static com.rooxteam.sso.aal.ConfigKeys.PRINCIPAL_PROVIDER_TYPE_DEFAULT;

@SuppressWarnings({"WeakerAccess"})
public class AalFactory {

    private AalFactory() {
    }

    /**
     * @param config конфигурация.
     *               Описание ключей конфигурации в {@link ConfigKeys}.
     * @return AuthenticationAuthorizationLibrary
     */
    public static AuthenticationAuthorizationLibrary create(Configuration config) {
        String authzTypeString = config.getString(AUTHORIZATION_TYPE, AUTHORIZATION_TYPE_DEFAULT);
        AuthorizationType authorizationType = AuthorizationType.valueOf(authzTypeString);
        String providerTypeString = config.getString(AAL_VALIDATION_TYPE, config.getString(PRINCIPAL_PROVIDER_TYPE, PRINCIPAL_PROVIDER_TYPE_DEFAULT));
        ProviderType providerType = ProviderType.valueOf(providerTypeString);
        return create(config, authorizationType, providerType);
    }


    /**
     * @param config            конфигурация.
     *                          Описание ключей конфигурации в {@link ConfigKeys}.
     * @param authorizationType CONFIG | JWT | OPA
     * @param providerType      principal provider type
     * @return AuthenticationAuthorizationLibrary
     */
    public static AuthenticationAuthorizationLibrary create(Configuration config,
                                                            AuthorizationType authorizationType,
                                                            ProviderType providerType) {
        Objects.requireNonNull(config);

        Timer pollingTimer = new Timer("RX Polling Timer", true);

        PoolingHttpClientConnectionManager connectionManager = createConnectionManager(config);

        RequestConfig requestConfig = createRequestConfig(config);
        ConnectionReuseStrategy reuseStrategy = createReuseStrategy(config);
        if (reuseStrategy == null) {
            throw new IllegalArgumentException();
        }

        CloseableHttpClient httpClient = createHttpClient(reuseStrategy, config, requestConfig, connectionManager);

        SsoAuthorizationClient authorizationClient = createSsoAuthorizationClient(authorizationType, config,
                httpClient);

        PrincipalProvider principalProvider = createPrincipalProvider(providerType, config, httpClient);
        AccessTokenValidator accessTokenValidator = new JwtTokenValidator(config, httpClient);

        SsoAuthenticationClient authenticationClient = new SsoAuthenticationClient(config, httpClient);
        SsoTokenClient tokenClient = new SsoTokenClient(config, httpClient);
        UserIpProvider userIpProvider = new UserIpProviderFactory(config).create();
        OtpClient otpClient = new OtpClient(config, httpClient, userIpProvider);

        return new RooxAuthenticationAuthorizationLibrary(config,
                pollingTimer,
                authorizationClient,
                authenticationClient,
                tokenClient,
                otpClient,
                principalProvider,
                accessTokenValidator,
                createMetricsIntegration());
    }

    private static MetricsIntegration createMetricsIntegration() {
        try {
            Class.forName("io.micrometer.core.instrument.MeterRegistry");
            return new MicrometerMetricsIntegration();
        } catch (ClassNotFoundException e) {
            return new NoOpMetricsIntegration();
        }
    }


    private static PoolingHttpClientConnectionManager createConnectionManager(Configuration config) {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(config.getInt(HTTP_CONNECTION_POOL_SIZE, HTTP_CONNECTION_POOL_SIZE_DEFAULT));
        connectionManager.setDefaultMaxPerRoute(config.getInt(HTTP_CONNECTION_POOL_SIZE_PER_ROUTE,
                HTTP_CONNECTION_POOL_SIZE_PER_ROUTE_DEFAULT));

        return connectionManager;
    }

    private static RequestConfig createRequestConfig(Configuration config) {
        return RequestConfig.custom()
                .setResponseTimeout((Timeout.ofMilliseconds(config.getInt(HTTP_SOCKET_TIMEOUT, HTTP_SOCKET_TIMEOUT_DEFAULT))))
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(config.getInt(HTTP_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT_DEFAULT)))
                .build();
    }

    private static ConnectionReuseStrategy createReuseStrategy(Configuration config) {
        String reuseStrategyString = config.getString(CONNECTION_REUSE_STRATEGY, CONNECTION_REUSE_STRATEGY_DEFAULT);
        com.rooxteam.sso.aal.ConnectionReuseStrategy connectionReuseStrategy
                = com.rooxteam.sso.aal.ConnectionReuseStrategy.valueOf(reuseStrategyString);
        if (connectionReuseStrategy == com.rooxteam.sso.aal.ConnectionReuseStrategy.NO_REUSE) {
            return new NoConnectionReuseStrategy();
        }

        if (connectionReuseStrategy == com.rooxteam.sso.aal.ConnectionReuseStrategy.KEEP_ALIVE) {
            return new DefaultConnectionReuseStrategy();
        }

        return null;
    }

    private static CloseableHttpClient createHttpClient(
            ConnectionReuseStrategy reuseStrategy,
            Configuration config,
            RequestConfig requestConfig,
            PoolingHttpClientConnectionManager connectionManager
    ) {
        CookieStore cookieStore;
        if (config.getBoolean("com.rooxteam.sso.aal.client.cookie.store.enablePerRequest", true)) {
            cookieStore = new RequestCookieStore();
        } else {
            cookieStore = new BasicCookieStore();
        }
        return HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultCookieStore(cookieStore)
                .setDefaultRequestConfig(requestConfig)
                .addRequestInterceptorLast(new MonitoringHttpClientRequestInterceptor(config))
                .setConnectionReuseStrategy(reuseStrategy)
                .build();
    }

    private static SsoAuthorizationClient createSsoAuthorizationClient(AuthorizationType authorizationType,
                                                                       Configuration config,
                                                                       CloseableHttpClient httpClient) {
        String classNameStringValue = getSsoAuthorizationClientName(authorizationType);

        SsoAuthorizationClient ssoAuthorizationClient;
        Class aClass;
        try {
            aClass = Class.forName(classNameStringValue);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }

        try {
            //noinspection unchecked
            ssoAuthorizationClient =
                    (SsoAuthorizationClient) aClass
                            .getConstructor(Configuration.class, CloseableHttpClient.class)
                            .newInstance(config, httpClient);
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Fail while creating policy provider. Check " +
                    "authorization_client_class_name property.");
        }

        return ssoAuthorizationClient;
    }

    private static PrincipalProvider createPrincipalProvider(ProviderType providerType,
                                                             Configuration configuration, CloseableHttpClient httpClient) {
        switch (providerType) {
            case INTROSPECTION:
                throw new IllegalArgumentException("unsupported yet");
            case JWT: {
                return new PrincipalJwtProviderImpl(new JwtTokenValidator(configuration, httpClient));
            }
            case USERINFO: {
                return new PrincipalUserInfoProviderImpl(configuration, httpClient, new JwtTokenValidator(configuration, httpClient));
            }
            default:
            case TOKENINFO: {
                return new PrincipalTokenInfoProviderImpl(configuration, httpClient,
                        new RequestContextCollector(new UserIpProviderFactory(configuration).create()),
                        new TokenClaimValidatorImpl(configuration));
            }
        }
    }

    private static String getSsoAuthorizationClientName(AuthorizationType authorizationType) {
        switch (authorizationType) {
            case OPA:
                return "com.rooxteam.sso.aal.opa.OpaAuthorizationClient";
            case CONFIG: {
                return "com.rooxteam.sso.aal.client.SsoAuthorizationClientByConfig";
            }
            default:
            case JWT: {
                return "com.rooxteam.sso.aal.client.SsoAuthorizationClientByJwt";
            }
        }
    }
}
