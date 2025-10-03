package com.rooxteam.sso.aal;

/**
 * Статус валидации JWT токена
 */
public enum ValidationStatus {
    VALID, INVALID_FORMAT, INVALID_SIGNATURE, EXPIRED
}
