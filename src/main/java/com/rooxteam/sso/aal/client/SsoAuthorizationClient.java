package com.rooxteam.sso.aal.client;

import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.client.model.EvaluationResponse;

import java.util.Map;

public interface SsoAuthorizationClient {

    EvaluationResponse isActionOnResourceAllowedByPolicy(String token, String resource, String method, Map<String, ?> env);

    EvaluationResponse isActionOnResourceAllowedByPolicy(Principal subject, String resourceName, String actionName);

    Principal validate(final String token);

}
