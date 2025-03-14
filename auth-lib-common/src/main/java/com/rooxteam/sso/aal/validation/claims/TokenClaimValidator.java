package com.rooxteam.sso.aal.validation.claims;

import java.util.Map;

/**
 * Валидатор клеймов токена.
 *
 * @author sergey.syroezhkin
 * @since 15.07.2024
 */
public interface TokenClaimValidator {

    ValidationResult validate(Map<String, Object> claims);

}
