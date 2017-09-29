package com.rooxteam.sso.aal.client;

import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.client.model.EvaluationRequest;
import com.rooxteam.sso.aal.client.model.EvaluationResponse;

import java.util.List;
import java.util.Map;

public interface SsoAuthorizationClient {

    EvaluationResponse isActionOnResourceAllowedByPolicy(Principal subject, String resource, String method, Map<String, ?> env);

    Map<EvaluationRequest, EvaluationResponse> whichActionAreAllowed(Principal subject, List<EvaluationRequest> policies);

    Principal validate(final String token);

}
