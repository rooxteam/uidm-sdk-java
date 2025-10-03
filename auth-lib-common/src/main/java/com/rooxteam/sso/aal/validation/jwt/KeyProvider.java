package com.rooxteam.sso.aal.validation.jwt;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.JWK;

public interface KeyProvider {
    JWK getKey(JWSHeader jwsHeader);
}
