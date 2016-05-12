package com.rooxteam.sso.aal.exception;

/**
 * {@code AalException} - базовая ошибка Aal
 */
public class AalException extends RuntimeException {

    public AalException(Exception e) {
        super("Suppressed exception", e);
    }

    public AalException(String message, Exception e) {
        super(message, e);
    }

    public AalException(String message) {
        super(message);
    }
}
