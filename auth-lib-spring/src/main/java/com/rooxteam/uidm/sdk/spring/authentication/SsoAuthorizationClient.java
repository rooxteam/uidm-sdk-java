package com.rooxteam.uidm.sdk.spring.authentication;

import javax.servlet.http.HttpServletRequest;

/**
 * SPI interface between spring filter and core AAL
 */
public interface SsoAuthorizationClient {

    AuthenticationState validate(HttpServletRequest request, String accessToken);

}
