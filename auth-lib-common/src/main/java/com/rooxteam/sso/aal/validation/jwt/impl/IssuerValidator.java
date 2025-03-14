package com.rooxteam.sso.aal.validation.jwt.impl;

import com.nimbusds.jwt.JWT;
import com.rooxteam.sso.aal.configuration.Configuration;
import com.rooxteam.sso.aal.validation.jwt.JwtValidatorSPI;
import com.rooxteam.sso.aal.validation.jwt.ValidationResult;
import lombok.SneakyThrows;
import lombok.ToString;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

import static com.rooxteam.sso.aal.AalLogger.LOG;
import static com.rooxteam.sso.aal.ConfigKeys.JWT_ISSUER;

/**
 * Validates iss if present;
 */
@ToString
@SuppressWarnings("unused")
public class IssuerValidator implements JwtValidatorSPI {

    private String serverIssuer;

    @Override
    public void configure(Configuration configuration, CloseableHttpClient httpClient) {
        this.serverIssuer = configuration.getString(JWT_ISSUER, "");
    }

    @SneakyThrows
    @Override
    public ValidationResult validate(JWT jwt) {
        String iss = jwt.getJWTClaimsSet().getIssuer();
        if (iss != null) {
            LOG.debugv("iss: {0}, correct: {1}", iss, serverIssuer);
            if (!serverIssuer.equals(iss)) {
                return ValidationResult.fail(ValidationResult.Reason.InvalidIssuer);
            }
        } else {
            LOG.debug("No iss. Skipping check");
        }
        return ValidationResult.success();
    }

}
