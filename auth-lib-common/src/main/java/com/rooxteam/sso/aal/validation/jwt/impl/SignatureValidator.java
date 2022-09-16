package com.rooxteam.sso.aal.validation.jwt.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.SignedJWT;
import com.rooxteam.sso.aal.configuration.Configuration;
import com.rooxteam.sso.aal.validation.jwt.JwtValidatorSPI;
import com.rooxteam.sso.aal.validation.jwt.KeyProvider;
import com.rooxteam.sso.aal.validation.jwt.ValidationResult;
import org.apache.http.impl.client.CloseableHttpClient;

import static com.rooxteam.sso.aal.AalLogger.LOG;

/**
 * Validates signature cryptographically
 */
@SuppressWarnings("unused")
public class SignatureValidator implements JwtValidatorSPI {

    private KeyProvider keyProvider;


    public SignatureValidator() {

    }

    @Override
    public void configure(Configuration configuration, CloseableHttpClient httpClient) {
        try {
            keyProvider = new JwksUrlKeyProvider(configuration);
        } catch (Exception e) {
            throw new RuntimeException("Failed to configure JwksUrlKeyProvider", e);
        }
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


        JWK key = keyProvider.getKey(signedJwt.getHeader());
        if (key == null) {
            return ValidationResult.fail(ValidationResult.Reason.KeyNotFound);
        }

        JWSVerifier signingHandler = createVerifier(alg, key);

        if (signingHandler == null) {
            LOG.warnv("Signing handler not supported for alg {0}", alg);
            return ValidationResult.fail(ValidationResult.Reason.UnsupportedAlg);
        }

        try {
            if (signedJwt.verify(signingHandler)) {
                return ValidationResult.success();
            } else {
                return ValidationResult.fail(ValidationResult.Reason.SignatureNotValid);
            }
        } catch (JOSEException e) {
            return ValidationResult.fail(ValidationResult.Reason.SignatureNotValid);
        }
    }

    private JWSVerifier createVerifier(JWSAlgorithm alg, JWK key) {
        if (alg == JWSAlgorithm.ES256 || alg == JWSAlgorithm.ES384 || alg == JWSAlgorithm.ES512) {
            if (key.getKeyType() == KeyType.EC) {
                try {
                    return new ECDSAVerifier(key.toECKey().toECPublicKey());
                } catch (JOSEException e) {
                    LOG.warnv("Failed to create verifier", e);
                }
            } else {
                LOG.warnv("Alg is {0} and key is of type {1}", alg, key.getKeyType());
            }
        }
        if (alg == JWSAlgorithm.RS256 || alg == JWSAlgorithm.RS384 || alg == JWSAlgorithm.RS512) {
            if (key.getKeyType() == KeyType.RSA) {
                try {
                    return new RSASSAVerifier(key.toRSAKey().toRSAPublicKey());
                } catch (JOSEException e) {
                    LOG.warnv("Failed to create verifier", e);
                }
            } else {
                LOG.warnv("Alg is {0} and key is of type {1}", alg, key.getKeyType());
            }
        }
        return null;
    }
}
