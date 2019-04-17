package com.rooxteam.sso.aal.exception;

public class ValidateException extends RuntimeException {

    public ValidateException(Exception e) {
        super("Suppressed exception", e);
    }

    public ValidateException(String message, Exception e) {
        super(message, e);
    }

    public ValidateException(String message) {
        super(message);
    }
}
