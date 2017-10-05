package com.rooxteam.sso.aal.utils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.rooxteam.sso.aal.client.model.Decision;
import com.rooxteam.sso.aal.client.model.EvaluationResponse;
import com.sun.identity.policy.ActionDecision;
import com.sun.identity.policy.PolicyDecision;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class SsoPolicyDecisionUtils {

    public static EvaluationResponse toEvaluationResponse(PolicyDecision result, String actionNaame) {
        ActionDecision decision = (ActionDecision) result.getActionDecisions().get(actionNaame);
        if (decision == null) {
            return new EvaluationResponse(Decision.Deny);
        }
        if (decision.getValues().contains("allow")) {
            return new EvaluationResponse(Decision.Permit);
        }
        Map<String, String> advices = parseAdvices(decision.getAdvices());
        return new EvaluationResponse(Decision.Deny, advices);
    }

    @SuppressWarnings("unchecked")
    protected static Map<String, String> parseAdvices(Map advices) {
        if (advices == null || advices.isEmpty()) {
            return Collections.emptyMap();
        }
        ImmutableMap.Builder<String, String> result = ImmutableMap.builder();
        for (Map.Entry entry : (Set<Map.Entry>) advices.entrySet()) {
            String name = (String) entry.getKey();
            Set values = (Set) entry.getValue();
            result.put(name, (String) Iterables.getFirst(values, null));
        }
        return result.build();
    }
}
