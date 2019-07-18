package com.rooxteam.uidm.sdk.servlet.service;

import com.rooxteam.sso.aal.Principal;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public interface ServletAuthFilterService {
    /**
     * This method attempts to extract access token from {@link HttpServletRequest}.
     * @param request to extract access token from.
     * @return Optional of extracted token.
     */
    Optional<String> extractAccessToken(HttpServletRequest request);

    /**
     * This method attempts to validate the access token and returns token claims in the form of {@link Principal} in case of success.
     * Will return empty optional if validation fails.
     * @param accessToken - access token to validate.
     * @return Optional with claims about principal.
     */
    Optional<Principal> authenticate(String accessToken);
}
