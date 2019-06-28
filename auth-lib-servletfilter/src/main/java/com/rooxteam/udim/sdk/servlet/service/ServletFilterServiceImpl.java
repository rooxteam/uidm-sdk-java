package com.rooxteam.udim.sdk.servlet.service;

import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.client.CommonSsoAuthorizationClient;
import com.rooxteam.sso.aal.exception.AuthorizationException;
import com.rooxteam.udim.sdk.servlet.configuration.ServletFilterServiceConfiguration;
import com.rooxteam.udim.sdk.servlet.util.ExtractAccessTokenUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.Set;

public class ServletFilterServiceImpl implements ServletFilterService {

    private CommonSsoAuthorizationClient commonSsoAuthorizationClient;
    private ServletFilterServiceConfiguration configuration;

    public ServletFilterServiceImpl(CommonSsoAuthorizationClient commonSsoAuthorizationClient, ServletFilterServiceConfiguration configuration) {
        this.commonSsoAuthorizationClient = commonSsoAuthorizationClient;
        this.configuration = configuration;

    }

    @Override
    public Optional<Principal> getPrincipal(HttpServletRequest request, String accessToken) {
        try {
            return Optional.of(commonSsoAuthorizationClient.validate(request, accessToken));
        } catch (AuthorizationException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> extractAccessToken(Cookie[] cookies, String headerValue) {
        Optional<String> token = Optional.empty();
        Set<String> cookieNames = configuration.getAuthorizationCookieNames();
        for (Cookie cookie : cookies) {
            if (cookieNames.contains(cookie.getName())) {
                token = Optional.ofNullable(cookie.getValue());
                break;
            }
        }
        if ((!token.isPresent() || "".equals(token.get())) && headerValue != null) {
            token = ExtractAccessTokenUtils.extractFromHeader(headerValue);
        }
        return token;
    }
}
