package com.rooxteam.uidm.sdk.spring.authorization;

import com.rooxteam.errors.auth.exception.AuthApiException;
import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.client.EvaluationContext;
import com.rooxteam.sso.aal.client.model.EvaluationResponse;
import com.rooxteam.sso.aal.realms.RealmDetermineService;
import com.rooxteam.uidm.sdk.spring.authentication.AuthenticationState;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PolicyEvaluationController {

    private static final String AAL_PRINCIPAL_ATTRIBUTE_NAME = "aalPrincipal";

    private final PolicyEvaluationService evaluationService;
    private final RealmDetermineService realmDetermineService;

    public PolicyEvaluationController(final PolicyEvaluationService evaluationService,
                                      final RealmDetermineService realmDetermineService) {
        this.evaluationService = evaluationService;
        this.realmDetermineService = realmDetermineService;
    }

    @RequestMapping(value = "/policyEvaluation", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public EvaluationResponse evaluate(@RequestBody EvaluationContext evaluationContextDTO,
                                       AuthenticationState authenticationState) {
        return evaluate(evaluationContextDTO, realmDetermineService.determine(), authenticationState);
    }

    @RequestMapping(value = "/realms/{realm}/policyEvaluation", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public EvaluationResponse evaluate(@RequestBody EvaluationContext evaluationContextDTO,
                                       @PathVariable String realm,
                                       AuthenticationState authenticationState) {
        if (!realm.equals(authenticationState.getRealm())) {
            throw new AuthApiException(403, "Realm not matched");
        }
        Principal principal = (Principal) authenticationState.getAttributes().get(AAL_PRINCIPAL_ATTRIBUTE_NAME);
        return evaluationService.getPolicyEvaluation(principal, evaluationContextDTO);
    }
}
