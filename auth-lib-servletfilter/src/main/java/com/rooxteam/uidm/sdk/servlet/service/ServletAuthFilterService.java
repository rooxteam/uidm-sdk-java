package com.rooxteam.uidm.sdk.servlet.service;

import com.rooxteam.sso.aal.Principal;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public interface ServletAuthFilterService {
    String trimAccessTokenForLogging(String token);
    Optional<String> extractAccessToken(HttpServletRequest request);
    Optional<Principal> authenticate(String accessToken);
}
