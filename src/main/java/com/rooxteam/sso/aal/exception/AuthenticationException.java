package com.rooxteam.sso.aal.exception;

import lombok.Getter;

/**
 * Throws when AAL can't authenticate customer. Details contains more information
 */
@Getter
public class AuthenticationException extends AalException {


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
     * @see com.rooxteam.sso.aal.exception.ErrorSubtypes
     */
    private String errorSubtype;


    public AuthenticationException(Exception e) {
        super(e);
    }

    public AuthenticationException(String message, Exception e) {
        super(message, e);
    }

    public AuthenticationException(String message) {
        super(message);
    }


    public AuthenticationException(String error, String errorDescription, String errorSubtype) {
        super(format("Authentication failed with error ''{0}'':''{1}'':''{2}''", error, errorSubtype, errorDescription));
        this.error = error;
        this.errorDescription = errorDescription;
        this.errorSubtype = errorSubtype;
    }

    @Override
    public String toString() {
        return "AuthenticationException{" +
                "error='" + error + '\'' +
                ", errorDescription='" + errorDescription + '\'' +
                ", errorSubtype='" + errorSubtype + '\'' +
                '}';
    }
}
