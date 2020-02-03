package com.rooxteam.sso.aal.exception;

/**
 * Exception throws on some network issues during requests to external systems.
 * E.g., service unavailable or returns 5xx error.
 *
 * @author sergey.syroezhkin
 * @since 03.02.2020
 */
public class AuthNetworkException extends Exception {
    private static final long serialVersionUID = -6373553869264351225L;

    public AuthNetworkException(String message) {
        super(message);
    }

    public AuthNetworkException(String message, Throwable cause) {
        super(message, cause);
    }

}
