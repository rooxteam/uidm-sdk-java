package com.rooxteam.sso.aal.client;

import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.client.model.EvaluationResponse;

import java.util.Map;

public interface SsoAuthorizationClient {

    EvaluationResponse isActionOnResourceAllowedByPolicy(Principal subject, String token, String resource, String method, Map<String, ?> env);

    Principal validate(final String token);

}
