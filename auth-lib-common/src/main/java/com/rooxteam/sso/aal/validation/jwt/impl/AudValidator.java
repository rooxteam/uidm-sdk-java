package com.rooxteam.sso.aal.validation.jwt.impl;

import com.nimbusds.jwt.JWT;
import com.rooxteam.sso.aal.configuration.Configuration;
import com.rooxteam.sso.aal.validation.jwt.JwtValidatorSPI;
import com.rooxteam.sso.aal.validation.jwt.ValidationResult;
import lombok.SneakyThrows;
import lombok.ToString;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static com.rooxteam.sso.aal.AalLogger.LOG;
import static com.rooxteam.sso.aal.ConfigKeys.ALLOWED_CLIENT_IDS;
import static com.rooxteam.sso.aal.ConfigKeys.CLIENT_ID;

/**
 * @author sergey.syroezhkin
 * @since 12.07.2024
 */
@ToString
public class AudValidator implements JwtValidatorSPI {
    private final Collection<String> allowedClientIds = new HashSet<>();

    @Override
    public void configure(Configuration configuration, CloseableHttpClient httpClient) {
        allowedClientIds.add(configuration.getString(CLIENT_ID));
        allowedClientIds.addAll(Optional.ofNullable(configuration.getStringArray(ALLOWED_CLIENT_IDS))
                .map(Arrays::asList).orElseGet(Collections::emptyList));
    }

    @SneakyThrows({ParseException.class})
    @Override
    public ValidationResult validate(JWT jwt) {
        List<String> audience = jwt.getJWTClaimsSet().getAudience();
        if (audience != null && !audience.isEmpty()) {
            LOG.debugv("audience: {0}, expected any: {1}", audience, allowedClientIds);
            if (!audience.stream().anyMatch(allowedClientIds::contains)) {
                LOG.warnAudienceIsNotAllowed(audience, this.allowedClientIds);
                return ValidationResult.fail(ValidationResult.Reason.InvalidAudience);
            }
        } else {
            LOG.warnNoAudience();
            return ValidationResult.fail(ValidationResult.Reason.InvalidAudience);
        }
        return ValidationResult.success();
    }
}
