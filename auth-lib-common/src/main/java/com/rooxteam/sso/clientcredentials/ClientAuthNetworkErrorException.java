package com.rooxteam.sso.clientcredentials;

import com.rooxteam.sso.aal.exception.AuthNetworkException;

/**
 * @author sergey.syroezhkin
 * @since 03.02.2020
 */
public class ClientAuthNetworkErrorException extends ClientAuthenticationException implements AuthNetworkException {
    private static final long serialVersionUID = 4730165852034108262L;

    ClientAuthNetworkErrorException(String message) {
        super(message);
    }

    ClientAuthNetworkErrorException(String message, Throwable cause) {
        super(message, cause);
    }

}
