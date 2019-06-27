package com.rooxteam.udim.sdk.servlet.service;

import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.PropertyScope;
import com.rooxteam.sso.aal.client.CommonSsoAuthorizationClient;
import com.rooxteam.sso.aal.exception.AuthorizationException;
import com.rooxteam.udim.sdk.servlet.configuration.ServletFilterServiceConfiguration;
import com.rooxteam.udim.sdk.servlet.dto.TokenInfo;
import com.rooxteam.udim.sdk.servlet.dto.TokenPropertyNames;
import com.rooxteam.udim.sdk.servlet.util.ExtractAccessTokenUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
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
    public Optional<TokenInfo> getAccessTokenInfo(HttpServletRequest request, String accessToken) {
        Principal principal;
        try {
            principal = commonSsoAuthorizationClient.validate(request, accessToken);
        } catch (AuthorizationException e) {
            return Optional.empty();
        }
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setPrincipal((String) principal.getProperty(PropertyScope.SHARED_IDENTITY_PARAMS, TokenPropertyNames.PRINCIPAL));
        tokenInfo.setAuthLevel(((List<String>) principal.getProperty(PropertyScope.SHARED_IDENTITY_PARAMS, TokenPropertyNames.AUTH_LEVEL)).get(0));
        tokenInfo.setRoles(((List<String>) principal.getProperty(PropertyScope.SHARED_IDENTITY_PARAMS, TokenPropertyNames.ROLES)));
        tokenInfo.setScopes(((List<String>) principal.getProperty(PropertyScope.SHARED_IDENTITY_PARAMS, TokenPropertyNames.SCOPES)));
        tokenInfo.setExpiresIn(principal.getExpirationTime().getTimeInMillis() / 1000);
        return Optional.of(tokenInfo);
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
        if (!token.isPresent() || "".equals(token.get())) {
            token = ExtractAccessTokenUtils.extractFromHeader(headerValue);
        }
        return token;
    }
}
