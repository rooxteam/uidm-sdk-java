package com.rooxteam.sso.aal.validation.jwt.impl;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.source.DefaultJWKSetCache;
import com.nimbusds.jose.jwk.source.JWKSetCache;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.SimpleSecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.configuration.Configuration;
import com.rooxteam.sso.aal.validation.jwt.KeyProvider;
import lombok.SneakyThrows;

import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class JwksUrlKeyProvider implements KeyProvider {


    private final RemoteJWKSet<SimpleSecurityContext> remoteJWKSet;

    @SneakyThrows
    public JwksUrlKeyProvider(Configuration configuration) {
        String url = configuration.getString(ConfigKeys.JWKS_URL);
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("JWKS url is not configured properly");
        }
        JWKSetCache jwkSetCache = new DefaultJWKSetCache(
                configuration.getInt(ConfigKeys.JWKS_CACHE_LIFESPAN, (int) DefaultJWKSetCache.DEFAULT_LIFESPAN_MINUTES),
                configuration.getInt(ConfigKeys.JWKS_CACHE_REFRESH_TIME, (int) DefaultJWKSetCache.DEFAULT_REFRESH_TIME_MINUTES),
                TimeUnit.MINUTES
        );
        ResourceRetriever resourceRetriever = new JwksResourceRetriever(
                configuration.getInt(ConfigKeys.JWKS_HTTP_CONNECT_TIMEOUT, RemoteJWKSet.DEFAULT_HTTP_CONNECT_TIMEOUT),
                configuration.getInt(ConfigKeys.JWKS_HTTP_READ_TIMEOUT, RemoteJWKSet.DEFAULT_HTTP_READ_TIMEOUT));
        remoteJWKSet = new RemoteJWKSet<>(new URL(url), resourceRetriever, jwkSetCache);
    }

    @SneakyThrows
    @Override
    public JWK getKey(JWSHeader jwsHeader) {
        List<JWK> list = remoteJWKSet.get(new JWKSelector(JWKMatcher.forJWSHeader(jwsHeader)), new SimpleSecurityContext());
        return list.isEmpty() ? null : list.get(0);
    }
}
