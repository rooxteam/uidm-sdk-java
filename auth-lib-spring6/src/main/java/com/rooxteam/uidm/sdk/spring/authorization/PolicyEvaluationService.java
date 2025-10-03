package com.rooxteam.uidm.sdk.spring.authorization;

import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.client.EvaluationContext;
import com.rooxteam.sso.aal.client.model.EvaluationResponse;

public interface PolicyEvaluationService {
    EvaluationResponse getPolicyEvaluation(Principal principal, EvaluationContext evaluationContextDTO);
}
