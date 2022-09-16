package com.rooxteam.sso.aal.validation.jwt.impl;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jwt.JWT;
import com.rooxteam.sso.aal.configuration.Configuration;
import com.rooxteam.sso.aal.validation.jwt.JwtValidatorSPI;
import com.rooxteam.sso.aal.validation.jwt.ValidationResult;
import org.apache.http.impl.client.CloseableHttpClient;

import static com.rooxteam.sso.aal.AalLogger.LOG;

/**
 * Validates that alg is not none.
 */
public class AlgNoneValidator implements JwtValidatorSPI {
    @Override
    public void configure(Configuration configuration, CloseableHttpClient httpClient) {

    }

    @Override
    public ValidationResult validate(JWT signedJwt) {
        Algorithm alg = signedJwt.getHeader().getAlgorithm();
        LOG.debugv("alg: {0}", alg);

        if (alg == Algorithm.NONE) {
            return ValidationResult.fail(ValidationResult.Reason.UnsupportedAlg);
        }
        return ValidationResult.success();
    }
}
