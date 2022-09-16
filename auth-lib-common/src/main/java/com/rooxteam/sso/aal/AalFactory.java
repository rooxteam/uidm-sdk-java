package com.rooxteam.sso.aal;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.rooxteam.compat.Objects;
import com.rooxteam.sso.aal.validation.AccessTokenValidator;
import com.rooxteam.sso.aal.validation.impl.JwtTokenValidator;
import com.rooxteam.sso.aal.validation.impl.TokeninfoTokenValidator;
import com.rooxteam.sso.aal.client.MonitoringHttpClientRequestInterceptor;
import com.rooxteam.sso.aal.client.OtpClient;
import com.rooxteam.sso.aal.client.SsoAuthenticationClient;
import com.rooxteam.sso.aal.client.SsoAuthorizationClient;
import com.rooxteam.sso.aal.client.SsoTokenClient;
import com.rooxteam.sso.aal.client.cookies.RequestCookieStore;
import com.rooxteam.sso.aal.client.model.EvaluationResponse;
import com.rooxteam.sso.aal.configuration.Configuration;
import com.rooxteam.sso.aal.metrics.MetricsIntegration;
import com.rooxteam.sso.aal.metrics.MicrometerMetricsIntegration;
import com.rooxteam.sso.aal.metrics.NoOpMetricsIntegration;
import com.rooxteam.sso.aal.userIp.UserIpProvider;
import com.rooxteam.sso.aal.userIp.UserIpProviderFactory;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.lang.reflect.InvocationTargetException;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import static com.rooxteam.sso.aal.ConfigKeys.*;

@SuppressWarnings({"WeakerAccess", "UnstableApiUsage"})
public class AalFactory {

    public static final String MISSING_PROPERTY_IN_CONFIGURATION = "Missing property ''{0}'' in configuration.";

    private AalFactory() {
    }

    /**
     * @param config конфигурация.
     *               Описание ключей конфигурации в {@link ConfigKeys}.
     * @return AuthenticationAuthorizationLibrary
     */
    public static AuthenticationAuthorizationLibrary create(Configuration config) {
        Objects.requireNonNull(config);

        String authzTypeString = config.getString(AUTHORIZATION_TYPE, AUTHORIZATION_TYPE_DEFAULT);
        AuthorizationType authorizationType = AuthorizationType.valueOf(authzTypeString);

        Timer pollingTimer = new Timer("RX Polling Timer", true);

        PoolingHttpClientConnectionManager connectionManager = createConnectionManager(config);

        RequestConfig requestConfig = createRequestConfig(config);

        org.apache.http.ConnectionReuseStrategy reuseStrategy = createReuseStrategy(config);
        if (reuseStrategy == null) {
            throw new IllegalArgumentException();
        }

        CloseableHttpClient httpClient = createHttpClient(reuseStrategy, config, requestConfig, connectionManager);

        SsoAuthorizationClient authorizationClient = createSsoAuthorizationClient(authorizationType, config,
                httpClient);

        String validationTypeString = config.getString(VALIDATION_TYPE, VALIDATION_TYPE_TYPE_DEFAULT);
        ValidationType validationType = ValidationType.valueOf(validationTypeString);

        AccessTokenValidator accessTokenValidator = createAccessTokenValidator(validationType, authorizationClient, config, httpClient);

        SsoAuthenticationClient authenticationClient = new SsoAuthenticationClient(config, httpClient);
        SsoTokenClient tokenClient = new SsoTokenClient(config, httpClient);
        UserIpProvider userIpProvider = new UserIpProviderFactory(config).create();
        OtpClient otpClient = new OtpClient(config, httpClient, userIpProvider);

        Cache<PolicyDecisionKey, EvaluationResponse> isAllowedPolicyDecisionsCache = initPoliciesCache(config);

        RooxAuthenticationAuthorizationLibrary aal = new RooxAuthenticationAuthorizationLibrary(config,
                pollingTimer,
                authorizationClient,
                authenticationClient,
                tokenClient,
                otpClient,
                isAllowedPolicyDecisionsCache,
                accessTokenValidator,
                createMetricsIntegration());
        // TODO: implement instantiation of metrics
        if (config.getBoolean(POLLING_ENABLED, POLLING_ENABLED_DEFAULT)) {
            int pollingDefaultTimeoutSeconds = config.getInt(POLLING_PERIOD, POLLING_PERIOD_DEFAULT);
            aal.enablePolling(pollingDefaultTimeoutSeconds, TimeUnit.SECONDS);
        }

        return aal;
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
                .setSocketTimeout(config.getInt(HTTP_SOCKET_TIMEOUT, HTTP_SOCKET_TIMEOUT_DEFAULT))
                .setConnectTimeout(config.getInt(HTTP_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT_DEFAULT))
                .build();
    }

    private static org.apache.http.ConnectionReuseStrategy createReuseStrategy(Configuration config) {
        String reuseStrategyString = config.getString(CONNECTION_REUSE_STRATEGY, CONNECTION_REUSE_STRATEGY_DEFAULT);
        ConnectionReuseStrategy connectionReuseStrategy = ConnectionReuseStrategy.valueOf(reuseStrategyString);
        if (connectionReuseStrategy == ConnectionReuseStrategy.NO_REUSE) {
            return new NoConnectionReuseStrategy();
        }

        if (connectionReuseStrategy == ConnectionReuseStrategy.KEEP_ALIVE) {
            return new DefaultConnectionReuseStrategy();
        }

        return null;
    }

    private static CloseableHttpClient createHttpClient(org.apache.http.ConnectionReuseStrategy reuseStrategy,
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
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCookieStore(cookieStore)
                .addInterceptorLast(new MonitoringHttpClientRequestInterceptor(config))
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
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Fail while creating policy provider. Check " +
                    "authorization_client_class_name property.");
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Fail while creating policy provider. Check " +
                    "authorization_client_class_name property.");
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("Fail while creating policy provider. Check " +
                    "authorization_client_class_name property.");
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException("Fail while creating policy provider. Check " +
                    "authorization_client_class_name property.");
        }

        return ssoAuthorizationClient;
    }

    private static AccessTokenValidator createAccessTokenValidator(ValidationType validationType,
                                                                   SsoAuthorizationClient ssoAuthorizationClient,
                                                                   Configuration configuration, CloseableHttpClient httpClient) {
        switch (validationType) {
            case INTROSPECTION:
                throw new IllegalArgumentException("unsupported yet");
            case JWT: {
                return new JwtTokenValidator(configuration, httpClient);
            }
            default:
            case TOKENINFO: {
                return new TokeninfoTokenValidator(ssoAuthorizationClient);
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



    private static Cache<PolicyDecisionKey, EvaluationResponse> initPoliciesCache(Configuration config) {
        int policyCacheSize = config.getInt(POLICY_CACHE_LIMIT, POLICY_CACHE_LIMIT_DEFAULT);
        int policyExpireAfterWrite = config.getInt(POLICY_CACHE_EXPIRE_AFTER_WRITE,
                POLICY_CACHE_EXPIRE_AFTER_WRITE_DEFAULT);
        Cache<PolicyDecisionKey, EvaluationResponse> isAllowedPolicyDecisionsCache =
                CacheBuilder.newBuilder()
                        .maximumSize(policyCacheSize)
                        .expireAfterWrite(policyExpireAfterWrite, TimeUnit.SECONDS)
                        .build();
        AalLogger.LOG.traceInitPolicyCacheWithSize(policyCacheSize);
        return isAllowedPolicyDecisionsCache;
    }
}
