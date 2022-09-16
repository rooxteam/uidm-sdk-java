package com.rooxteam.sso.aal.validation;

import com.rooxteam.sso.aal.Principal;

import javax.servlet.http.HttpServletRequest;

public interface AccessTokenValidator {

    Principal validate(HttpServletRequest request, String token);
}
