package com.rooxteam.sso.aal.validation.jwt.impl;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.SimpleSecurityContext;
import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.configuration.Configuration;
import com.rooxteam.sso.aal.validation.jwt.KeyProvider;
import lombok.SneakyThrows;

import java.net.URL;
import java.util.List;

public class JwksUrlKeyProvider implements KeyProvider {


    private final RemoteJWKSet remoteJWKSet;

    @SneakyThrows
    public JwksUrlKeyProvider(Configuration configuration) {
        String url = configuration.getString(ConfigKeys.JWKS_URL);
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("JWK url is not configured properly");
        }
        remoteJWKSet = new RemoteJWKSet<>(new URL(url));
    }

    @SneakyThrows
    @Override
    public JWK getKey(JWSHeader jwsHeader) {
        List<JWK> list = remoteJWKSet.get(new JWKSelector(JWKMatcher.forJWSHeader(jwsHeader)), new SimpleSecurityContext());
        return list.isEmpty() ? null : list.get(0);
    }
}
