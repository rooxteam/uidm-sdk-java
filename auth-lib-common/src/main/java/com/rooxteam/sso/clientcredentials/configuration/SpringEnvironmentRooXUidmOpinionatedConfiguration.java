package com.rooxteam.sso.clientcredentials.configuration;

import com.rooxteam.compat.Objects;
import com.rooxteam.sso.aal.ConnectionReuseStrategy;
import com.rooxteam.sso.clientcredentials.ValidationType;
import org.springframework.core.env.Environment;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static com.rooxteam.sso.aal.ConfigKeys.ACCESS_TOKEN_PATH;
import static com.rooxteam.sso.aal.ConfigKeys.ACCESS_TOKEN_PATH_DEFAULT;
import static com.rooxteam.sso.aal.ConfigKeys.CLIENT_CREDENTIALS_CACHE_ENABLED;
import static com.rooxteam.sso.aal.ConfigKeys.CLIENT_CREDENTIALS_CACHE_ENABLED_DEFAULT;
import static com.rooxteam.sso.aal.ConfigKeys.CLIENT_CREDENTIALS_VALIDATION_TYPE;
import static com.rooxteam.sso.aal.ConfigKeys.CLIENT_CREDENTIALS_VALIDATION_TYPE_DEFAULT;
import static com.rooxteam.sso.aal.ConfigKeys.CLIENT_ID;
import static com.rooxteam.sso.aal.ConfigKeys.CLIENT_SECRET;
import static com.rooxteam.sso.aal.ConfigKeys.CONNECTION_REUSE_STRATEGY;
import static com.rooxteam.sso.aal.ConfigKeys.CONNECTION_REUSE_STRATEGY_DEFAULT;
import static com.rooxteam.sso.aal.ConfigKeys.COOKIE_STORE_ENABLE_PER_REQUEST;
import static com.rooxteam.sso.aal.ConfigKeys.COOKIE_STORE_ENABLE_PER_REQUEST_DEFAULT;
import static com.rooxteam.sso.aal.ConfigKeys.HTTP_CONNECTION_POOL_SIZE;
import static com.rooxteam.sso.aal.ConfigKeys.HTTP_CONNECTION_POOL_SIZE_DEFAULT;
import static com.rooxteam.sso.aal.ConfigKeys.HTTP_CONNECTION_POOL_SIZE_PER_ROUTE;
import static com.rooxteam.sso.aal.ConfigKeys.HTTP_CONNECTION_POOL_SIZE_PER_ROUTE_DEFAULT;
import static com.rooxteam.sso.aal.ConfigKeys.HTTP_CONNECTION_TIMEOUT;
import static com.rooxteam.sso.aal.ConfigKeys.HTTP_CONNECTION_TIMEOUT_DEFAULT;
import static com.rooxteam.sso.aal.ConfigKeys.HTTP_SOCKET_TIMEOUT;
import static com.rooxteam.sso.aal.ConfigKeys.HTTP_SOCKET_TIMEOUT_DEFAULT;
import static com.rooxteam.sso.aal.ConfigKeys.LEGACY_MASKING_ENABLED;
import static com.rooxteam.sso.aal.ConfigKeys.LEGACY_MASKING_ENABLED_DEFAULT;
import static com.rooxteam.sso.aal.ConfigKeys.REALM;
import static com.rooxteam.sso.aal.ConfigKeys.REALM_DEFAULT;
import static com.rooxteam.sso.aal.ConfigKeys.SSO_URL;
import static com.rooxteam.sso.aal.ConfigKeys.TOKEN_EXCHANGE_PATH;
import static com.rooxteam.sso.aal.ConfigKeys.TOKEN_EXCHANGE_PATH_DEFAULT;
import static com.rooxteam.sso.aal.ConfigKeys.TOKEN_INFO_PATH;
import static com.rooxteam.sso.aal.ConfigKeys.TOKEN_INFO_PATH_DEFAULT;
import static com.rooxteam.sso.aal.ConfigKeys.UPDATE_TIME_BEFORE_TOKEN_EXPIRATION;
import static com.rooxteam.sso.aal.ConfigKeys.UPDATE_TIME_BEFORE_TOKEN_EXPIRATION_DEFAULT;
import static java.text.MessageFormat.format;

/**
 * Implementation that instantiates configuration from Spring Env using required keys:
 * {@value com.rooxteam.sso.aal.ConfigKeys#SSO_URL} UIDM Base Url<p>
 * {@value com.rooxteam.sso.aal.ConfigKeys#CLIENT_ID} UIDM Client Id<p>
 * {@value com.rooxteam.sso.aal.ConfigKeys#CLIENT_SECRET} UIDM Client Secret<p>
 * And optional keys<p>
 * {@value com.rooxteam.sso.aal.ConfigKeys#REALM} UIDM Realm (defaults to {@value com.rooxteam.sso.aal.ConfigKeys#REALM_DEFAULT})<p>
 * {@value com.rooxteam.sso.aal.ConfigKeys#HTTP_CONNECTION_TIMEOUT} Connection timeout in ms (defaults to {@value com.rooxteam.sso.aal.ConfigKeys#HTTP_CONNECTION_TIMEOUT_DEFAULT})<p>
 * {@value com.rooxteam.sso.aal.ConfigKeys#HTTP_SOCKET_TIMEOUT} Read timeout in ms (defaults to {@value com.rooxteam.sso.aal.ConfigKeys#HTTP_SOCKET_TIMEOUT_DEFAULT})<p>
 * {@value com.rooxteam.sso.aal.ConfigKeys#HTTP_CONNECTION_POOL_SIZE} Pool size (defaults to {@value com.rooxteam.sso.aal.ConfigKeys#HTTP_CONNECTION_POOL_SIZE_DEFAULT})<p>
 * {@value com.rooxteam.sso.aal.ConfigKeys#CLIENT_CREDENTIALS_CACHE_ENABLED} Cache enabled (defaults to {@value com.rooxteam.sso.aal.ConfigKeys#CLIENT_CREDENTIALS_CACHE_ENABLED_DEFAULT})<p>
 * {@value com.rooxteam.sso.aal.ConfigKeys#UPDATE_TIME_BEFORE_TOKEN_EXPIRATION} Update time before expiration (defaults to {@value com.rooxteam.sso.aal.ConfigKeys#UPDATE_TIME_BEFORE_TOKEN_EXPIRATION_DEFAULT})<p>
 */
@SuppressWarnings("unused")
public final class SpringEnvironmentRooXUidmOpinionatedConfiguration implements Configuration {

    private final Environment environment;

    public SpringEnvironmentRooXUidmOpinionatedConfiguration(final Environment environment) {
        this.environment = Objects.requireNonNull(environment, "environment");
    }

    @Override
    public String getClientSecret(String clientId) {
        return environment.getProperty(format("com.rooxteam.aal.auth.client.{0}.password", clientId));
    }

    @Override
    public URI getAccessTokenEndpoint() {
        return UriComponentsBuilder
                .fromHttpUrl(environment.getProperty(SSO_URL))
                .path(environment.getProperty(ACCESS_TOKEN_PATH, ACCESS_TOKEN_PATH_DEFAULT))
                .build()
                .toUri();
    }

    @Override
    public URI getTokenExchangeEndpoint() {
        return UriComponentsBuilder
                .fromHttpUrl(environment.getProperty(SSO_URL))
                .path(environment.getProperty(TOKEN_EXCHANGE_PATH, TOKEN_EXCHANGE_PATH_DEFAULT))
                .build()
                .toUri();
    }

    @Override
    public URI getTokenValidationEndpoint() {
        return UriComponentsBuilder
                .fromHttpUrl(environment.getProperty(SSO_URL))
                .path(environment.getProperty(TOKEN_INFO_PATH, TOKEN_INFO_PATH_DEFAULT))
                .build()
                .toUri();
    }

    @Override
    public String getClientId() {
        return environment.getProperty(CLIENT_ID);
    }

    @Override
    public String getClientSecret() {
        return environment.getProperty(CLIENT_SECRET);
    }

    @Override
    public String getUidmRealm() {
        return environment.getProperty(REALM, REALM_DEFAULT);
    }

    @Override
    public Map<String, String> getAdditionalRequestParameters() {
        return new HashMap<String, String>();
    }

    @Override
    public String getHeaderPrefix() {
        return "Bearer sso_1.0_";
    }

    @Override
    public int getConnectTimeout() {
        return environment.getProperty(HTTP_CONNECTION_TIMEOUT, Integer.class, HTTP_CONNECTION_TIMEOUT_DEFAULT);
    }

    @Override
    public int getReadTimeout() {
        return environment.getProperty(HTTP_SOCKET_TIMEOUT, Integer.class, HTTP_SOCKET_TIMEOUT_DEFAULT);
    }

    @Override
    public int getPoolSize() {
        return environment.getProperty(HTTP_CONNECTION_POOL_SIZE, Integer.class, HTTP_CONNECTION_POOL_SIZE_DEFAULT);
    }

    @Override
    public boolean isTokensCacheEnabled() {
        return environment.getProperty(CLIENT_CREDENTIALS_CACHE_ENABLED, Boolean.class, CLIENT_CREDENTIALS_CACHE_ENABLED_DEFAULT);
    }

    @Override
    public int getUpdateTimeBeforeTokenExpiration() {
        return environment.getProperty(UPDATE_TIME_BEFORE_TOKEN_EXPIRATION, Integer.class, UPDATE_TIME_BEFORE_TOKEN_EXPIRATION_DEFAULT);
    }

    @Override
    public boolean legacyMaskingEnabled() {
        return environment.getProperty(LEGACY_MASKING_ENABLED, Boolean.class, LEGACY_MASKING_ENABLED_DEFAULT);
    }

    @Override
    public ValidationType getValidationType() {
        return ValidationType.valueOf(environment.getProperty(CLIENT_CREDENTIALS_VALIDATION_TYPE, CLIENT_CREDENTIALS_VALIDATION_TYPE_DEFAULT));
    }

    @Override
    public int getPoolSizePerRoute() {
        return environment.getProperty(HTTP_CONNECTION_POOL_SIZE_PER_ROUTE, Integer.class, HTTP_CONNECTION_POOL_SIZE_PER_ROUTE_DEFAULT);
    }

    @Override
    public ConnectionReuseStrategy getConnectionReuseStrategy() {
        String stringValue = environment.getProperty(CONNECTION_REUSE_STRATEGY, CONNECTION_REUSE_STRATEGY_DEFAULT);
        return ConnectionReuseStrategy.valueOf(stringValue);
    }

    @Override
    public boolean isCookieStorePerRequestEnabled() {
        return environment.getProperty(COOKIE_STORE_ENABLE_PER_REQUEST, Boolean.class, COOKIE_STORE_ENABLE_PER_REQUEST_DEFAULT);
    }
}
