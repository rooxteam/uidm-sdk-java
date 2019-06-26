package com.rooxteam.udim.sdk.servlet.service;

import com.rooxteam.udim.sdk.servlet.dto.TokenInfo;

import java.util.Optional;

public interface ValidateTokenService {
    /**
     * Attempts to validate access token. Returns empty Optional if networks fails or access token is not valid.
     * @param tokenInfoUrl - /tokeinfo endpoint
     * @param accessToken - user access token extracted from request.
     * @return Optional of token information.
     */
    Optional<TokenInfo> getAccessTokenInfo(String tokenInfoUrl, String accessToken);

    /**
     * Attempts to extract access token from header.
     * @param headerValue - value of Authorization header from request.
     * @return Optional of extracted token.
     */
    Optional<String> extractAccessToken(String headerValue);
}
