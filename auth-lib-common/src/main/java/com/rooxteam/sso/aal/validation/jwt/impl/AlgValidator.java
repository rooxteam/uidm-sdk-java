package com.rooxteam.sso.aal.validation.jwt.impl;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jwt.JWT;
import com.rooxteam.sso.aal.configuration.Configuration;
import com.rooxteam.sso.aal.validation.jwt.JwtValidatorSPI;
import com.rooxteam.sso.aal.validation.jwt.ValidationResult;
import lombok.ToString;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static com.rooxteam.sso.aal.ConfigKeys.JWT_VALIDATION_ALLOWED_ALGORITHMS;

/**
 * Validates that alg in jwt.
 */
@ToString
@SuppressWarnings("unused")
public class AlgValidator implements JwtValidatorSPI {

    private Set<Algorithm> allowedAlgorithms;

    @Override
    public void configure(Configuration configuration, CloseableHttpClient httpClient) {
        this.allowedAlgorithms = Arrays.stream(configuration.getStringArray(JWT_VALIDATION_ALLOWED_ALGORITHMS))
                .map(Algorithm::parse)
                .collect(Collectors.toSet());
    }

    @Override
    public ValidationResult validate(JWT jwt) {
        Algorithm alg = jwt.getHeader().getAlgorithm();
        if (allowedAlgorithms.contains(alg)) {
            return ValidationResult.success();
        } else {
            return ValidationResult.fail(ValidationResult.Reason.UnsupportedAlg);
        }
    }
}
