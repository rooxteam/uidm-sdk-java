package com.rooxteam.uidm.sdk.spring.authentication;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

public interface SsoAuthorizationClient {

    AuthenticationState validate(HttpServletRequest request, String jwt);

    Map<String, Set<String>> getAttributesIfAllowed(String ssoToken, String resource, String method, Map<String, Object> envParameters);


}
