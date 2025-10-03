package com.rooxteam.sso.aal.validation.claims;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * @author sergey.syroezhkin
 * @since 15.07.2024
 */
@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public final class ValidationResult {

    private final boolean success;
    private final Reason reason;

    public static ValidationResult success() {
        return new ValidationResult(true, Reason.Success);
    }

    public static ValidationResult fail(Reason reason) {
        return new ValidationResult(false, reason);
    }

    public enum Reason {
        Success, InvalidClientId
    }

}
