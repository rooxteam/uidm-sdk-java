package com.rooxteam.uidm.sdk.spring.authentication;

import com.rooxteam.sso.aal.exception.AuthenticationException;
import com.rooxteam.sso.aal.utils.StringUtils;
import com.rooxteam.util.RealmFormatValidator;

import java.util.Optional;

/**
 * Класс для валидации realm значения (например realm значения из url) и значения в AuthenticationState,
 * который заполняется при аутентификации
 */
public final class RealmValidator {

    private RealmValidator() {
    }

    /**
     * Validates realm
     *
     * @param realm          realm value to validate
     * @param authentication Authentication state that is populated during authentication flow
     */
    public static void validateRealm(String realm, AuthenticationState authentication) {
        if (RealmFormatValidator.isRealmFormatValid(realm)) {
            String authenticationRealm = Optional.ofNullable(authentication)
                    .map(AuthenticationState::getRealm)
                    .filter(StringUtils::isNotBlank)
                    .orElse(null);
            if (!realm.equals(authenticationRealm)) {
                throw new AuthenticationException("Realm not matched");
            }
        } else {
            throw new AuthenticationException("Realm not valid");
        }
    }
}