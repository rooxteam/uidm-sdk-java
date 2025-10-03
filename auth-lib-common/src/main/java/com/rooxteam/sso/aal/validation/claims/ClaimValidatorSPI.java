package com.rooxteam.sso.aal.validation.claims;

import com.rooxteam.sso.aal.configuration.Configuration;

import java.util.Map;

/**
 * SPI для реализации различных типов валидаций клеймов токена.
 *
 * @author sergey.syroezhkin
 * @since 15.07.2024
 */
public interface ClaimValidatorSPI {

    void configure(Configuration configuration);

    ValidationResult validate(Map<String, Object> claims);
}
