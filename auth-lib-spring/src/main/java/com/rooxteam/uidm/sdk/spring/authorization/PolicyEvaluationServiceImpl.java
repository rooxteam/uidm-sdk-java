package com.rooxteam.uidm.sdk.spring.authorization;

import com.rooxteam.sso.aal.AuthenticationAuthorizationLibrary;
import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.client.EvaluationContext;
import com.rooxteam.sso.aal.client.model.EvaluationResponse;

public final class PolicyEvaluationServiceImpl implements PolicyEvaluationService {

    private final AuthenticationAuthorizationLibrary aal;

    public PolicyEvaluationServiceImpl(final AuthenticationAuthorizationLibrary aal) {
        this.aal = aal;
    }

    @Override
    public EvaluationResponse getPolicyEvaluation(Principal principal, EvaluationContext evaluationContextDTO) {
        return aal.evaluatePolicy(principal,
                evaluationContextDTO.getResourceName(),
                evaluationContextDTO.getActionName(),
                evaluationContextDTO.getEnvParams()
        );
    }
}
