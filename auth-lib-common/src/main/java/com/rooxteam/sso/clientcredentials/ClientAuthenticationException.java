package com.rooxteam.sso.clientcredentials;

final class ClientAuthenticationException extends Exception {

    ClientAuthenticationException(String message) {
        super(message);
    }

    ClientAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
