package com.rooxteam.sso.clientcredentials;

@SuppressWarnings("WeakerAccess")
public class ClientAuthenticationException extends Exception {

    ClientAuthenticationException(String message) {
        super(message);
    }

    ClientAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
