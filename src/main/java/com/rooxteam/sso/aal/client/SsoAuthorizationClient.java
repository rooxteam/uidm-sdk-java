package com.rooxteam.sso.aal.client;

import com.iplanet.sso.SSOToken;
import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.client.model.EvaluationResponse;

import java.util.Map;

public interface SsoAuthorizationClient {

    EvaluationResponse isActionOnResourceAllowedByPolicy(SSOToken ssoToken, String resource, String method);

    EvaluationResponse isActionOnResourceAllowedByPolicy(String jwtToken, String resource, String method, Map<String, ?> env);

    EvaluationResponse isActionOnResourceAllowedByPolicy(Principal subject, String resourceName, String actionName);

    void invalidateSSOSession(SSOToken ssoToken);

    Principal validate(final String jwtToken);

    SSOToken authenticateByJwt(String jwt);

}
