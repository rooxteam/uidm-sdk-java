package com.rooxteam.uidm.sdk.spring.authorization;

import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.client.EvaluationContext;
import com.rooxteam.sso.aal.client.model.EvaluationResponse;
import com.rooxteam.uidm.sdk.spring.authentication.AuthenticationState;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/policyEvaluation")
public class PolicyEvaluationController {

    private static final String AAL_PRINCIPAL_ATTRIBUTE_NAME = "aalPrincipal";

    private final PolicyEvaluationService evaluationService;

    public PolicyEvaluationController(final PolicyEvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public EvaluationResponse evaluate(@RequestBody EvaluationContext evaluationContextDTO,
                                       AuthenticationState authenticationState) {
        Principal principal = (Principal) authenticationState.getAttributes().get(AAL_PRINCIPAL_ATTRIBUTE_NAME);
        return evaluationService.getPolicyEvaluation(principal, evaluationContextDTO);
    }
}
