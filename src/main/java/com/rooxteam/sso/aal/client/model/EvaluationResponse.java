package com.rooxteam.sso.aal.client.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Map;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResponse {
    private Decision decision;
    private Map<String, String> advices;

    public EvaluationResponse(Decision decision) {
        this.decision = decision;
        this.advices = Collections.emptyMap();
    }
}

