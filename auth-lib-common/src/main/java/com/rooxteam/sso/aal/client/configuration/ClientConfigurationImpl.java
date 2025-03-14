package com.rooxteam.sso.aal.client.configuration;

import com.rooxteam.compat.Objects;
import com.rooxteam.sso.aal.ConnectionReuseStrategy;
import com.rooxteam.sso.aal.configuration.Configuration;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static com.rooxteam.sso.aal.ConfigKeys.ACCESS_TOKEN_PATH;
import static com.rooxteam.sso.aal.ConfigKeys.ACCESS_TOKEN_PATH_DEFAULT;
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
import static com.rooxteam.sso.aal.ConfigKeys.REALM;
import static com.rooxteam.sso.aal.ConfigKeys.REALM_DEFAULT;
import static com.rooxteam.sso.aal.ConfigKeys.SSO_URL;
import static com.rooxteam.sso.aal.ConfigKeys.TOKEN_EXCHANGE_PATH;
import static com.rooxteam.sso.aal.ConfigKeys.TOKEN_EXCHANGE_PATH_DEFAULT;
import static com.rooxteam.sso.aal.ConfigKeys.TOKEN_INFO_PATH;
import static com.rooxteam.sso.aal.ConfigKeys.TOKEN_INFO_PATH_DEFAULT;
import static java.text.MessageFormat.format;

/**
 * Implementation that instantiates configuration from {@see com.rooxteam.sso.aal.configuration.Configuration} using required keys:
 * {@value com.rooxteam.sso.aal.ConfigKeys#SSO_URL} UIDM Base Url<p>
 * {@value com.rooxteam.sso.aal.ConfigKeys#CLIENT_ID} UIDM Client Id<p>
 * {@value com.rooxteam.sso.aal.ConfigKeys#CLIENT_SECRET} UIDM Client Secret<p>
 * And optional keys<p>
 * {@value com.rooxteam.sso.aal.ConfigKeys#REALM} UIDM Realm (defaults to {@value com.rooxteam.sso.aal.ConfigKeys#REALM_DEFAULT})<p>
 * {@value com.rooxteam.sso.aal.ConfigKeys#HTTP_CONNECTION_TIMEOUT} Connection timeout in ms (defaults to {@value com.rooxteam.sso.aal.ConfigKeys#HTTP_CONNECTION_TIMEOUT_DEFAULT})<p>
 * {@value com.rooxteam.sso.aal.ConfigKeys#HTTP_SOCKET_TIMEOUT} Read timeout in ms (defaults to {@value com.rooxteam.sso.aal.ConfigKeys#HTTP_SOCKET_TIMEOUT_DEFAULT})<p>
 * {@value com.rooxteam.sso.aal.ConfigKeys#HTTP_CONNECTION_POOL_SIZE} Pool size (defaults to {@value com.rooxteam.sso.aal.ConfigKeys#HTTP_CONNECTION_POOL_SIZE_DEFAULT})<p>
 */
final class ClientConfigurationImpl implements ClientConfiguration {

    private final Configuration configuration;

    public ClientConfigurationImpl(final Configuration configuration) {
        this.configuration = Objects.requireNonNull(configuration, "environment");
    }

    @Override
    public String getClientSecret(String clientId) {
        return configuration.getString(format("com.rooxteam.aal.auth.client.{0}.password", clientId));
    }

    @Override
    public URI getAccessTokenEndpoint() {
        return UriComponentsBuilder
                .fromHttpUrl(configuration.getString(SSO_URL))
                .path(configuration.getString(ACCESS_TOKEN_PATH, ACCESS_TOKEN_PATH_DEFAULT))
                .build()
                .toUri();
    }

    @Override
    public URI getTokenValidationEndpoint() {
        return UriComponentsBuilder
                .fromHttpUrl(configuration.getString(SSO_URL))
                .path(configuration.getString(TOKEN_INFO_PATH, TOKEN_INFO_PATH_DEFAULT))
                .build()
                .toUri();
    }

    @Override
    public URI getTokenExchangeEndpoint() {
        return UriComponentsBuilder
                .fromHttpUrl(configuration.getString(SSO_URL))
                .path(configuration.getString(TOKEN_EXCHANGE_PATH, TOKEN_EXCHANGE_PATH_DEFAULT))
                .build()
                .toUri();
    }

    @Override
    public String getUidmRealm() {
        return configuration.getString(REALM, REALM_DEFAULT);
    }

    @Override
    public String getHeaderPrefix() {
        return "Bearer ";
    }

    @Override
    public int getConnectTimeout() {
        return configuration.getInt(HTTP_CONNECTION_TIMEOUT, HTTP_CONNECTION_TIMEOUT_DEFAULT);
    }

    @Override
    public int getReadTimeout() {
        return configuration.getInt(HTTP_SOCKET_TIMEOUT, HTTP_SOCKET_TIMEOUT_DEFAULT);
    }

    @Override
    public int getPoolSize() {
        return configuration.getInt(HTTP_CONNECTION_POOL_SIZE, HTTP_CONNECTION_POOL_SIZE_DEFAULT);
    }

    @Override
    public int getPoolSizePerRoute() {
        return configuration.getInt(HTTP_CONNECTION_POOL_SIZE_PER_ROUTE, HTTP_CONNECTION_POOL_SIZE_PER_ROUTE_DEFAULT);
    }

    @Override
    public ConnectionReuseStrategy getConnectionReuseStrategy() {
        String stringValue = configuration.getString(CONNECTION_REUSE_STRATEGY, CONNECTION_REUSE_STRATEGY_DEFAULT);
        return ConnectionReuseStrategy.valueOf(stringValue);
    }

    @Override
    public boolean isCookieStorePerRequestEnabled() {
        return configuration.getBoolean(COOKIE_STORE_ENABLE_PER_REQUEST, COOKIE_STORE_ENABLE_PER_REQUEST_DEFAULT);
    }
}
