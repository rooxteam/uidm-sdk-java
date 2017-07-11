package com.rooxteam.sso.aal;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.rooxteam.jwt.IatClaimChecker;
import com.rooxteam.jwt.NbfClaimChecker;
import com.rooxteam.jwt.StringClaimChecker;
import com.rooxteam.sso.aal.client.*;
import com.rooxteam.sso.aal.client.model.EvaluationResponse;
import org.apache.commons.configuration.Configuration;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.lang.reflect.InvocationTargetException;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import static com.rooxteam.sso.aal.ConfigKeys.*;
import static java.text.MessageFormat.format;

public class AalFactory {

    private static final String AGENT_APP_USERNAME = "com.sun.identity.agents.app.username";
    private static final String OPENAM_SERVICE_PASSWORD = "com.iplanet.am.service.password";
    private static final String IPLANET_NAMING_URL = "com.iplanet.am.naming.url";
    private static final String OPENAM_AGENTS_POLLING_INTERVAL = "com.sun.identity.agents.polling.interval";
    private static final String OPENAM_AGENTS_POLLING_INTERVAL_VALUE = "3";
    private static final String NAMING_SERVICE_PATH = "/namingservice";
    public static final String MISSING_PROPERTY_IN_CONFIGURATION = "Missing property ''{0}'' in configuration.";
    private static final String OPENAM_CACHE_MODE = "com.sun.identity.policy.client.cacheMode";
    private static final String OPENAM_CACHE_MODE_SELF = "self";

    private AalFactory() {
    }

    /**
     * @param config конфигурация. Описание ключей конфигурации в {@link ConfigKeys}.
     * @return AuthenticationAuthorizationLibrary
     */
    public static AuthenticationAuthorizationLibrary create(Configuration config) {

        String authTypeString = config.getString(AUTHORIZATION_TYPE, AUTHORIZATION_TYPE_DEFAULT);
        AuthorizationType authorizationType = AuthorizationType.valueOf(authTypeString);
        if (authorizationType == AuthorizationType.SSO_TOKEN) {
            // for AuthorizationType.SSO_TOKEN we have to initialize OpenAM AuthContext 
            setOpenamSystemProperties(config);
        }

        Timer pollingTimer = new Timer("RX Polling Timer", true);

        PoolingHttpClientConnectionManager connectionManager = createConnectionManager(config);

        RequestConfig requestConfig = createRequestConfig(config);

        org.apache.http.ConnectionReuseStrategy reuseStrategy = createReuseStrategy(config);
        if (reuseStrategy == null) {
            throw new IllegalArgumentException();
        }

        CloseableHttpClient httpClient = createHttpClient(reuseStrategy, config, requestConfig, connectionManager);

        SsoAuthorizationClient authorizationClient = createSsoAuthorizationClient(authorizationType, config, httpClient);
        SsoAuthenticationClient authenticationClient = new SsoAuthenticationClient(config, httpClient);
        SsoTokenClient tokenClient = new SsoTokenClient(config, httpClient);
        OtpClient otpClient = new OtpClient(config, httpClient);

        Cache<PrincipalKey, Principal> principalCache = initPrincipalsCache(config, authorizationClient);
        Cache<PolicyDecisionKey, EvaluationResponse> isAllowedPolicyDecisionsCache = initPoliciesCache(config);
        JwtValidator jwtValidator = createJwtValidator(config);

        RooxAuthenticationAuthorizationLibrary aal = new RooxAuthenticationAuthorizationLibrary(pollingTimer,
                authorizationClient,
                authenticationClient,
                tokenClient,
                otpClient,
                isAllowedPolicyDecisionsCache,
                principalCache,
                jwtValidator,
                authorizationType);

        if (config.getBoolean(POLLING_ENABLED, POLLING_ENABLED_DEFAULT)) {
            int pollingDefaultTimeoutSeconds = config.getInt(POLLING_PERIOD, POLLING_PERIOD_DEFAULT);
            aal.enablePolling(pollingDefaultTimeoutSeconds, TimeUnit.SECONDS);
        }

        return aal;
    }


    private static PoolingHttpClientConnectionManager createConnectionManager(Configuration config) {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(config.getInt(HTTP_CONNECTION_POOL_SIZE, HTTP_CONNECTION_POOL_SIZE_DEFAULT));
        connectionManager.setDefaultMaxPerRoute(config.getInt(HTTP_CONNECTION_POOL_SIZE_PER_ROUTE, HTTP_CONNECTION_POOL_SIZE_PER_ROUTE_DEFAULT));

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
        return HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .addInterceptorLast(new MonitoringHttpClientRequestInterceptor(config))
                .setConnectionReuseStrategy(reuseStrategy)
                .build();
    }

    private static JwtValidator createJwtValidator(Configuration config) {
        String sharedKey = config.getString(SHARED_KEY);
        NbfClaimChecker nbfClaimChecker = new NbfClaimChecker();
        IatClaimChecker iatClaimChecker = new IatClaimChecker();
        String issuer = config.getString(JWT_ISSUER);
        if (issuer == null) {
            throw new IllegalStateException(format(MISSING_PROPERTY_IN_CONFIGURATION, JWT_ISSUER));
        }
        StringClaimChecker issuerClaimChecker = new StringClaimChecker("iss", issuer, true);
        return new JwtValidator(sharedKey, nbfClaimChecker, iatClaimChecker, issuerClaimChecker);
    }

    private static SsoAuthorizationClient createSsoAuthorizationClient(AuthorizationType authorizationType,
                                                                       Configuration config,
                                                                       CloseableHttpClient httpClient) {
        String classNameStringValue = getSsoAuthorizationClientName(authorizationType);

        SsoAuthorizationClient ssoAuthorizationClient = null;
        Class aClass;
        try {
            aClass = Class.forName(classNameStringValue);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }

        try {
            ssoAuthorizationClient =
                    (SsoAuthorizationClient) aClass
                            .getConstructor(Configuration.class, CloseableHttpClient.class)
                            .newInstance(config, httpClient);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new IllegalArgumentException("Fail while creating policy provider. Check authorization_client_class_name property.");
        }

        return ssoAuthorizationClient;
    }

    private static String getSsoAuthorizationClientName(AuthorizationType authorizationType) {
        switch (authorizationType) {
            default:
            case SSO_TOKEN: {
                return "com.rooxteam.sso.aal.client.SsoAuthorizationClientBySSOToken";
            }
            case JWT: {
                return "com.rooxteam.sso.aal.client.SsoAuthorizationClientByJwt";
            }
            case CONFIG: {
                return "com.rooxteam.sso.aal.client.SsoAuthorizationClientByConfig";
            }
        }
    }

    private static void setOpenamSystemProperties(Configuration config) {
        String ssoUrl = config.getString(SSO_URL);
        if (ssoUrl != null) {
            System.setProperty(IPLANET_NAMING_URL, ssoUrl + NAMING_SERVICE_PATH);
        }
        String openamAgentName = config.getString(OPENAM_AGENT_NAME);
        if (openamAgentName != null) {
            System.setProperty(AGENT_APP_USERNAME, openamAgentName);
        }
        String openamAgentPassword = config.getString(OPENAM_AGENT_PASSWORD);
        if (openamAgentPassword != null) {
            System.setProperty(OPENAM_SERVICE_PASSWORD, openamAgentPassword);
        }
        System.setProperty(OPENAM_AGENTS_POLLING_INTERVAL, OPENAM_AGENTS_POLLING_INTERVAL_VALUE);
        System.setProperty(OPENAM_CACHE_MODE, OPENAM_CACHE_MODE_SELF);
    }

    private static Cache<PrincipalKey, Principal> initPrincipalsCache(Configuration config, SsoAuthorizationClient authorizationClient) {
        int principalCacheSize = config.getInt(PRINCIPAL_CACHE_LIMIT, PRINCIPAL_CACHE_LIMIT_DEFAULT);
        int principalExpireAfterWrite = config.getInt(PRINCIPAL_CACHE_EXPIRE_AFTER_WRITE, PRINCIPAL_CACHE_EXPIRE_AFTER_WRITE_DEFAULT);
        Cache<PrincipalKey, Principal> principalCache =
                CacheBuilder.newBuilder()
                        .maximumSize(principalCacheSize)
                        .expireAfterWrite(principalExpireAfterWrite, TimeUnit.SECONDS)
//                        .removalListener(new SSOSessionInvalidator(authorizationClient))
                        .build();
        AalLogger.LOG.traceInitPrincipalCacheWithSize(principalCacheSize);
        return principalCache;
    }

    private static Cache<PolicyDecisionKey, EvaluationResponse> initPoliciesCache(Configuration config) {
        int policyCacheSize = config.getInt(POLICY_CACHE_LIMIT, POLICY_CACHE_LIMIT_DEFAULT);
        int policyExpireAfterWrite = config.getInt(POLICY_CACHE_EXPIRE_AFTER_WRITE, POLICY_CACHE_EXPIRE_AFTER_WRITE_DEFAULT);
        Cache<PolicyDecisionKey, EvaluationResponse> isAllowedPolicyDecisionsCache =
                CacheBuilder.newBuilder()
                        .maximumSize(policyCacheSize)
                        .expireAfterWrite(policyExpireAfterWrite, TimeUnit.SECONDS)
                        .build();
        AalLogger.LOG.traceInitPolicyCacheWithSize(policyCacheSize);
        return isAllowedPolicyDecisionsCache;
    }
}
