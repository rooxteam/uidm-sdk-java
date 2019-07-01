package com.rooxteam.uidm.sdk.servlet.filter;

import com.rooxteam.sso.aal.AalFactory;
import com.rooxteam.sso.aal.AuthParamType;
import com.rooxteam.sso.aal.AuthenticationAuthorizationLibrary;
import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.exception.AuthorizationException;
import com.rooxteam.uidm.sdk.servlet.configuration.AalConfigurationAdapter;
import com.rooxteam.uidm.sdk.servlet.configuration.FilterConfigKeys;
import com.rooxteam.uidm.sdk.servlet.util.ExtractAccessTokenUtils;

import javax.servlet.FilterConfig;
import javax.servlet.http.Cookie;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

class ServletFilterHelper {
    private FilterConfig config;
    private AuthenticationAuthorizationLibrary aal;

    ServletFilterHelper(FilterConfig filterConfig) {
        this.config = filterConfig;
        this.aal = AalFactory.create(new AalConfigurationAdapter(filterConfig));
    }

    ServletFilterHelper(FilterConfig filterConfig, AuthenticationAuthorizationLibrary aal) {
        this.config = filterConfig;
        this.aal = aal;
    }

    Optional<Principal> getPrincipal(String accessToken) {
        Map<String, String> params = new TreeMap<>();
        params.put(AuthParamType.JWT.getValue(), accessToken);
        try {
            return Optional.of(aal.authenticate(params));
        } catch (AuthorizationException e) {
            return Optional.empty();
        }
    }

    Optional<String> extractAccessToken(Cookie[] cookies, String headerValue) {
        Optional<String> token = Optional.empty();
        Set<String> cookieNames = getCookieNames();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieNames.contains(cookie.getName())) {
                    token = Optional.ofNullable(cookie.getValue());
                    break;
                }
            }
        }
        if ((!token.isPresent() || "".equals(token.get())) && headerValue != null) {
            token = ExtractAccessTokenUtils.extractFromHeader(headerValue);
        }
        return token;
    }

    private Set<String> getCookieNames() {
        String str = config.getInitParameter(FilterConfigKeys.AUTHORIZATION_COOKIE_NAMES_KEY);
        if (str != null && !str.isEmpty()) {
            TreeSet<String> set = new TreeSet<>();
            Collections.addAll(set, str.split(","));
            return set;
        } else {
            return new TreeSet<>();
        }
    }
}
