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
     * Метод не рекомендуется к использованию и скоро будет удален
     * Функционал по валидации с ValidationResult добавлен в AalAuthorizationClient
     * Функционал по предоставлению Principal по методу tokenInfo добавлен в PrincipalTokenInfoProviderImpl, AalAuthorizationClient
     */
    @Deprecated
    Principal validate(HttpServletRequest request, final String token);

    String postprocess(Principal subject, String resourceName, String actionName, Map<String, ?> envParameters, String response);
}
