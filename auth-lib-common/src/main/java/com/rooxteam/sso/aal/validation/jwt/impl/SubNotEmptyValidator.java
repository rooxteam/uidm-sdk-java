package com.rooxteam.sso.aal.validation.jwt.impl;

import com.nimbusds.jwt.JWT;
import com.rooxteam.sso.aal.configuration.Configuration;
import com.rooxteam.sso.aal.validation.jwt.JwtValidatorSPI;
import com.rooxteam.sso.aal.validation.jwt.ValidationResult;
import lombok.SneakyThrows;
import lombok.ToString;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

/**
 * Validates sub if present;
 */
@ToString
@SuppressWarnings("unused")
public class SubNotEmptyValidator implements JwtValidatorSPI {

    @Override
    public void configure(Configuration configuration, CloseableHttpClient httpClient) {

    }

    @SneakyThrows
    @Override
    public ValidationResult validate(JWT jwt) {
        String sub = jwt.getJWTClaimsSet().getSubject();
        if (sub == null || sub.isEmpty()) {
            return ValidationResult.fail(ValidationResult.Reason.InvalidSub);
        }
        return ValidationResult.success();
    }

}
