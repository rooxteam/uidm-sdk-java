package com.rooxteam.sso.aal.validation.jwt.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.SignedJWT;
import com.rooxteam.sso.aal.configuration.Configuration;
import com.rooxteam.sso.aal.validation.jwt.JwtValidatorSPI;
import com.rooxteam.sso.aal.validation.jwt.ValidationResult;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static com.rooxteam.sso.aal.AalLogger.LOG;
import static com.rooxteam.sso.aal.ConfigKeys.JWT_VALIDATION_HS_SHARED_SECRET;


@SuppressWarnings("unused")
public class HsSignatureValidator implements JwtValidatorSPI {

    private byte[] sharedSecret;

    public HsSignatureValidator() {

    }

    @Override
    public void configure(Configuration configuration, CloseableHttpClient httpClient) {
        sharedSecret = Optional.ofNullable(configuration.getString(JWT_VALIDATION_HS_SHARED_SECRET))
                .map(s -> s.getBytes(StandardCharsets.UTF_8))
                .orElseThrow(() -> new RuntimeException("Failed to configure HsSignatureValidator. SharedSecret is empty"));
    }


    @Override
    public ValidationResult validate(JWT jwt) {
        if (!(jwt instanceof SignedJWT)) {
            return ValidationResult.fail(ValidationResult.Reason.TokenIsNotSigned);
        }

        SignedJWT signedJwt = (SignedJWT) jwt;
        String kid = signedJwt.getHeader().getKeyID();
        LOG.debugv("kid: {0}", kid);

        JWSAlgorithm alg = signedJwt.getHeader().getAlgorithm();

        JWSVerifier signingHandler = createVerifier(alg);

        if (signingHandler == null) {
            LOG.warnv("Signing handler not supported for alg {0}", alg);
            return ValidationResult.fail(ValidationResult.Reason.UnsupportedAlg);
        }

        try {
            if (signedJwt.verify(signingHandler)) {
                return ValidationResult.success();
            } else {
                LOG.warn("JWT validation failed");
                return ValidationResult.fail(ValidationResult.Reason.SignatureNotValid);
            }
        } catch (JOSEException e) {
            LOG.warn("JWT validation failed with exception", e);
            return ValidationResult.fail(ValidationResult.Reason.SignatureNotValid);
        }
    }

    private JWSVerifier createVerifier(JWSAlgorithm alg) {
        if (alg == JWSAlgorithm.HS256 || alg == JWSAlgorithm.HS384 || alg == JWSAlgorithm.HS512) {
            if (sharedSecret == null) {
                LOG.warnv("Failed to create verifier, sharedSecret is not configured");
            }
            try {
                return new MACVerifier(sharedSecret);
            } catch (JOSEException e) {
                LOG.warnv("Failed to create verifier", e);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "HsSignatureValidator{" +
                "sharedSecret[" + sharedSecret.length +
                "]}";
    }
}
