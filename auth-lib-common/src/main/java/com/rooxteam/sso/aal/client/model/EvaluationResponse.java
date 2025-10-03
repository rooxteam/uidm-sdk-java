package com.rooxteam.sso.aal.client.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collections;
import java.util.Map;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class EvaluationResponse {
    private Decision decision;
    private Map<String, String> advices;
    private Map<String, Object> claims;

    public EvaluationResponse(Decision decision) {
        this(decision, Collections.<String, String>emptyMap(), Collections.<String, Object>emptyMap());
    }

    public EvaluationResponse(Decision decision, Map<String, String> advices) {
        this(decision, advices, Collections.<String, Object>emptyMap());
    }

    public EvaluationResponse(Decision decision, Map<String, String> advices, Map<String, Object> claims) {
        this.decision = decision;
        this.advices = advices;
        this.claims = claims;
    }
}
