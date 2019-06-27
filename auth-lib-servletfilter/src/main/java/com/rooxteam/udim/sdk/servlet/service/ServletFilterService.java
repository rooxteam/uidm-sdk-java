package com.rooxteam.udim.sdk.servlet.service;

import com.rooxteam.udim.sdk.servlet.dto.TokenInfo;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public interface ServletFilterService {
    /**
     * Attempts to validate access token. Returns empty Optional if networks fails or access token is not valid.
     * @param request - http request.
     * @param accessToken - user access token extracted from request.
     * @return Optional of token information.
     */
    Optional<TokenInfo> getAccessTokenInfo(HttpServletRequest request, String accessToken);

    /**
     * Attempts to extract access token cookie or from header.
     * @param cookies - cookie from request to search access token in.
     * @param headerValue - value of Authorization header from request.
     * @return Optional of extracted token.
     */
    Optional<String> extractAccessToken(Cookie[] cookies, String headerValue);

}
