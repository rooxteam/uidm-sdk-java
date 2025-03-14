package com.rooxteam.sso.aal.validation.jwt.impl;

import com.nimbusds.jwt.JWT;
import com.rooxteam.sso.aal.configuration.Configuration;
import com.rooxteam.sso.aal.validation.jwt.JwtValidatorSPI;
import com.rooxteam.sso.aal.validation.jwt.ValidationResult;
import lombok.SneakyThrows;
import lombok.ToString;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

import java.time.Clock;
import java.util.Date;

import static com.rooxteam.sso.aal.AalLogger.LOG;
import static com.rooxteam.sso.aal.ConfigKeys.JWT_VALIDATION_CLOCK_SKEW;

/**
 * Validates nbf and exp if present;
 */
@ToString(exclude = "clock")
@SuppressWarnings("unused")
public class TimeIntervalValidator implements JwtValidatorSPI {

    private final Clock clock;

    private int clockSkew;

    /**
     * Special constructor for tests
     * @param clock class
     */
    public TimeIntervalValidator(Clock clock) {
        this.clock = clock;
    }

    /**
     * Main constructor. Uses system time as time source.
     */
    public TimeIntervalValidator() {
        this.clock = Clock.systemUTC();
    }

    @Override
    public void configure(Configuration configuration, CloseableHttpClient httpClient) {
        this.clockSkew = configuration.getInt(JWT_VALIDATION_CLOCK_SKEW, 0) * 1000;
    }

    @SneakyThrows
    @Override
    public ValidationResult validate(JWT signedJwt) {
        Date now = new Date(clock.millis());
        Date nbf = signedJwt.getJWTClaimsSet().getNotBeforeTime();
        if (nbf != null) {
            LOG.debugv("nbf: {0}, current time: {1}", nbf, now);
            if (now.before(nbf)) {
                return ValidationResult.fail(ValidationResult.Reason.BeforeNotBefore);
            }
        } else {
            LOG.debug("No nbf. Skipping check");
        }

        Date nowWithClockSkew = new Date(clock.millis() + clockSkew);
        Date exp = signedJwt.getJWTClaimsSet().getExpirationTime();
        if (exp != null) {
            LOG.debugv("exp: {0}, current time with clockSkew: {1} clockSkew: {2}", exp, nowWithClockSkew, clockSkew);
            if (exp.before(nowWithClockSkew)) {
                return ValidationResult.fail(ValidationResult.Reason.Expired);
            }
        } else {
            LOG.debug("No exp. Skipping check");
        }
        return ValidationResult.success();
    }
}
