package com.rooxteam.uidm.sdk.servlet.service;

import com.rooxteam.sso.aal.AalFactory;
import com.rooxteam.sso.aal.AuthParamType;
import com.rooxteam.sso.aal.AuthenticationAuthorizationLibrary;
import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.exception.AuthorizationException;
import com.rooxteam.uidm.sdk.servlet.AuthFilterLogger;
import com.rooxteam.uidm.sdk.servlet.configuration.AalConfigurationAdapter;
import com.rooxteam.uidm.sdk.servlet.configuration.FilterConfigKeys;
import com.rooxteam.uidm.sdk.servlet.util.LoggerUtils;
import com.rooxteam.uidm.sdk.servlet.util.ParseStringUtils;

import javax.servlet.FilterConfig;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class ServletAuthFilterServiceImpl implements ServletAuthFilterService {
    private FilterConfig config;
    private AuthenticationAuthorizationLibrary aal;
    private Set<String> cookieNames;

    public ServletAuthFilterServiceImpl(FilterConfig filterConfig) {
        this.config = filterConfig;
        this.aal = AalFactory.create(new AalConfigurationAdapter(filterConfig));
        this.cookieNames = getCookieNames(filterConfig);
    }

    public ServletAuthFilterServiceImpl(FilterConfig filterConfig, AuthenticationAuthorizationLibrary aal) {
        this.config = filterConfig;
        this.aal = aal;
        this.cookieNames = getCookieNames(filterConfig);
    }

    public Optional<Principal> authenticate(String accessToken) {
        try {
            return Optional.ofNullable(aal.validate(accessToken));
        } catch (AuthorizationException e) {
            AuthFilterLogger.LOG.errorAuthentication(LoggerUtils.trimAccessTokenForLogging(accessToken), e);
            return Optional.empty();
        }
    }

    public Optional<String> extractAccessToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String headerValue = request.getHeader("Authorization");
        Optional<String> token = Optional.empty();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieNames.contains(cookie.getName())) {
                    token = Optional.ofNullable(cookie.getValue());
                    break;
                }
            }
        }
        if ((!token.isPresent() || "".equals(token.get())) && headerValue != null) {
            token = ParseStringUtils.parseAuthorizationHeader(headerValue);
        }
        return token;
    }

    private Set<String> getCookieNames(FilterConfig filterConfig) {
        String str = filterConfig.getInitParameter(FilterConfigKeys.AUTHORIZATION_COOKIE_NAMES_KEY);
        if (str != null && !str.isEmpty()) {
            return new TreeSet<>(ParseStringUtils.parseConfigValueAsList(str));
        } else {
            return new TreeSet<>();
        }
    }
}
