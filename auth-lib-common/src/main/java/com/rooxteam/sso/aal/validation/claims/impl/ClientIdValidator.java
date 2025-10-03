package com.rooxteam.sso.aal.validation.claims.impl;

import com.rooxteam.sso.aal.configuration.Configuration;
import com.rooxteam.sso.aal.validation.claims.ClaimValidatorSPI;
import com.rooxteam.sso.aal.validation.claims.ValidationResult;
import lombok.ToString;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import static com.rooxteam.sso.aal.AalLogger.LOG;
import static com.rooxteam.sso.aal.ConfigKeys.ALLOWED_CLIENT_IDS;
import static com.rooxteam.sso.aal.ConfigKeys.CLIENT_ID;

/**
 * Валидатор клейма client_id для токена.
 * Клейм должен совпадать (с учетом регистра) со значением прописанным в конфиге приложения.
 * Данный валидатор позволяет избежать использования "чужих" токенов, выданных для другого client_id.
 *
 * @author sergey.syroezhkin
 * @since 15.07.2024
 */
@ToString
public class ClientIdValidator implements ClaimValidatorSPI {
    private static final String CLIENT_ID_CLAIM_NAME = "client_id";

    private final Collection<String> allowedClientIds = new HashSet<>();

    @Override
    public void configure(Configuration configuration) {
        allowedClientIds.add(configuration.getString(CLIENT_ID));
        allowedClientIds.addAll(Optional.ofNullable(configuration.getStringArray(ALLOWED_CLIENT_IDS))
                .map(Arrays::asList).orElseGet(Collections::emptyList));
    }

    @Override
    public ValidationResult validate(Map<String, Object> claims) {
        String value = Optional.ofNullable(claims.get(CLIENT_ID_CLAIM_NAME)).map(String::valueOf).orElse(null);
        if (value != null) {
            LOG.debugv("''client_id'' claim value: {0}, expected: {1}", value, this.allowedClientIds);
            if (!allowedClientIds.contains(value)) {
                LOG.warnClientIdIsNotAllowed(value, this.allowedClientIds);
                return ValidationResult.fail(ValidationResult.Reason.InvalidClientId);
            }
        } else {
            LOG.warnNoClientId();
            return ValidationResult.fail(ValidationResult.Reason.InvalidClientId);
        }
        return ValidationResult.success();
    }
}
