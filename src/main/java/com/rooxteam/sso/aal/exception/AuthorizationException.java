package com.rooxteam.sso.aal.exception;

import lombok.Getter;

/**
 * Throws when AAL can't authorize customer. Details contains more information
 */
@Getter
public class AuthorizationException extends AalException {


    /**
     * Error type (see OAuth2.0 specification for 'error' field).
     * Mostly used values are 'invalid_grant' - customer provided invalid credentials, is blocked (customer error) and
     * 'invalid_client' -  application can't authenticate itself, usually configuration  error.
     */
    private String error;

    /**
     * Error human readable description (see OAuth2.0 specification for 'error_description' field)
     */
    private String errorDescription;

    /**
     * Error subtype. Machine readable string for reason. AAL client should process only known error subtypes or ignore it completely.
     * Error subtype may not be specified and contain null or empty string.
     * @see ErrorSubtypes
     */
    private String errorSubtype;


    public AuthorizationException(Exception e) {
        super(e);
    }

    public AuthorizationException(String message, Exception e) {
        super(message, e);
    }

    public AuthorizationException(String message) {
        super(message);
    }


    public AuthorizationException(String error, String errorDescription, String errorSubtype) {
        super("Authentication failed, see error and error subtype");
        this.error = error;
        this.errorDescription = errorDescription;
        this.errorSubtype = errorSubtype;
    }

    @Override
    public String toString() {
        return "AuthorizationException{" +
                "error='" + error + '\'' +
                ", errorDescription='" + errorDescription + '\'' +
                ", errorSubtype='" + errorSubtype + '\'' +
                '}';
    }
}
