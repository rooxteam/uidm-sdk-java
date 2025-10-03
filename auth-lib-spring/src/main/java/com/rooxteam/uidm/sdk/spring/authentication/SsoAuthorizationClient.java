package com.rooxteam.uidm.sdk.spring.authentication;

import com.nimbusds.jwt.JWT;
import com.rooxteam.sso.aal.validation.jwt.ValidationResult;

import javax.servlet.http.HttpServletRequest;

/**
 * SPI interface between spring filter and core AAL
 */
public interface SsoAuthorizationClient {

    AuthenticationState getPreAuthenticatedUserState(HttpServletRequest request, String accessToken);

    ValidationResult validateJWT(JWT jwtToken);

}
