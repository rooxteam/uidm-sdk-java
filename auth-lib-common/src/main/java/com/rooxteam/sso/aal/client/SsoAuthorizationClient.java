package com.rooxteam.sso.aal.client;

import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.client.model.EvaluationRequest;
import com.rooxteam.sso.aal.client.model.EvaluationResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface SsoAuthorizationClient {

    EvaluationResponse isActionOnResourceAllowedByPolicy(Principal subject, String resource, String method, Map<String, ?> env);

    Map<EvaluationRequest, EvaluationResponse> whichActionAreAllowed(Principal subject, List<EvaluationRequest> policies);

    /**
     * Token validation
     *
     * @param request Request
     * @param token   Token value
     * @return True if token is valid
     */
    Principal validate(HttpServletRequest request, final String token);

    String postprocess(Principal subject, String resourceName, String actionName, Map<String, ?> envParameters, String response);
}
