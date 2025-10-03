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
 * Validates iat if present;
 */
@ToString(exclude = "clock")
@SuppressWarnings("unused")
public class IssueTimeValidator implements JwtValidatorSPI {

    private final Clock clock;

    private int clockSkew;

    /**
     * Main constructor. Uses system time as time source.
     */
    public IssueTimeValidator() {
        this.clock = Clock.systemUTC();
    }

    /**
     * Special constructor for tests
     * @param clock class
     */
    public IssueTimeValidator(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void configure(Configuration configuration, CloseableHttpClient httpClient) {
        this.clockSkew = configuration.getInt(JWT_VALIDATION_CLOCK_SKEW, 0) * 1000;
    }

    @SneakyThrows
    @Override
    public ValidationResult validate(JWT jwt) {
        Date nowWithClockSkew = new Date(clock.millis() + clockSkew);
        Date iat = jwt.getJWTClaimsSet().getIssueTime();
        if (iat != null) {
            LOG.debugv("iat: {0}, current time with clockSkew: {1} clockSkew: {2}", iat, nowWithClockSkew, clockSkew);
            if (iat.after(nowWithClockSkew)) {
                return ValidationResult.fail(ValidationResult.Reason.InvalidIssueTime);
            }
        } else {
            LOG.debug("No iat. Skipping check");
        }
        return ValidationResult.success();
    }

}
