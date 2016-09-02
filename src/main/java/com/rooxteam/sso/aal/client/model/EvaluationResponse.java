package com.rooxteam.sso.aal.client.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;

@Getter
@AllArgsConstructor
public class EvaluationResponse {

    private final Decision decision;
    private final Map<String, String> advices;

    public EvaluationResponse(Decision decision) {
        this.decision = decision;
        this.advices = Collections.emptyMap();
    }
}

