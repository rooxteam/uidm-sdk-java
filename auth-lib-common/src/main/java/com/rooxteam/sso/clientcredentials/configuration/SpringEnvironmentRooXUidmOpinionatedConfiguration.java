package com.rooxteam.sso.clientcredentials.configuration;

import com.rooxteam.compat.Objects;
import org.springframework.core.env.Environment;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static com.rooxteam.sso.aal.ConfigKeys.CLIENT_ID;
import static com.rooxteam.sso.aal.ConfigKeys.CLIENT_SECRET;
import static com.rooxteam.sso.aal.ConfigKeys.HTTP_CONNECTION_POOL_SIZE;
import static com.rooxteam.sso.aal.ConfigKeys.HTTP_CONNECTION_POOL_SIZE_DEFAULT;
import static com.rooxteam.sso.aal.ConfigKeys.HTTP_CONNECTION_TIMEOUT;
import static com.rooxteam.sso.aal.ConfigKeys.HTTP_CONNECTION_TIMEOUT_DEFAULT;
import static com.rooxteam.sso.aal.ConfigKeys.HTTP_SOCKET_TIMEOUT;
import static com.rooxteam.sso.aal.ConfigKeys.HTTP_SOCKET_TIMEOUT_DEFAULT;
import static com.rooxteam.sso.aal.ConfigKeys.REALM;
import static com.rooxteam.sso.aal.ConfigKeys.REALM_DEFAULT;
import static com.rooxteam.sso.aal.ConfigKeys.SSO_URL;

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
 */
public final class SpringEnvironmentRooXUidmOpinionatedConfiguration implements Configuration {

    private final Environment environment;

    public SpringEnvironmentRooXUidmOpinionatedConfiguration(final Environment environment) {
        this.environment = Objects.requireNonNull(environment, "environment");
    }

    @Override
    public URI getAccessTokenEndpoint() {
        return UriComponentsBuilder
                .fromHttpUrl(environment.getProperty(SSO_URL))
                .pathSegment("oauth2", "access_token")
                .build()
                .toUri();
    }

    @Override
    public URI getTokenValidationEndpoint() {
        return UriComponentsBuilder
                .fromHttpUrl(environment.getProperty(SSO_URL))
                .pathSegment("oauth2", "tokeninfo")
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
}
