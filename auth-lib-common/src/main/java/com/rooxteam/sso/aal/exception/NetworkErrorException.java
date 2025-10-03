package com.rooxteam.sso.aal.exception;

/**
 * Exception throws on some network issues during requests to external systems.
 * E.g., service unavailable or returns unexpected error.
 *
 * @author sergey.syroezhkin
 * @since 03.02.2020
 */
public class NetworkErrorException extends AalException {
    private static final long serialVersionUID = 4411130316248633350L;

    public NetworkErrorException(Exception e) {
        this("Network error", e);
    }

    public NetworkErrorException(String message) {
        super(message);
    }

    public NetworkErrorException(String message, Exception e) {
        super(message, e);
    }
}
