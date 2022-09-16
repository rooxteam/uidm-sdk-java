package com.rooxteam.sso.aal.validation.impl;

import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.client.SsoAuthorizationClient;
import com.rooxteam.sso.aal.validation.AccessTokenValidator;

import javax.servlet.http.HttpServletRequest;

public class TokeninfoTokenValidator implements AccessTokenValidator {

    private final SsoAuthorizationClient ssoAuthorizationClient;

    public TokeninfoTokenValidator(SsoAuthorizationClient ssoAuthorizationClient) {
        //TODO move validation logic out of SsoAuthorizationClient to this class
        this.ssoAuthorizationClient = ssoAuthorizationClient;
    }

    @Override
    public Principal validate(HttpServletRequest request, String token) {
        return ssoAuthorizationClient.validate(request, token);
    }
}
