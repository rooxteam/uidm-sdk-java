package com.rooxteam.sso.aal.client.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class EvaluationResponse {
    private Decision decision;
    private Map<String, String> advices;
    private Map<String, Object> claims;

    public EvaluationResponse(Decision decision) {
        this(decision, new HashMap<String, String>(), null);
    }

    public EvaluationResponse(Decision decision, Map<String, String> advices) {
        this(decision, advices, null);
    }

    public EvaluationResponse(Decision decision, Map<String, String> advices, Map<String, Object> claims) {
        this.decision = decision;
        this.advices = advices;
        this.claims = claims;
    }
}
