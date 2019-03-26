package com.rooxteam.sso.aal.client.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.Value;

@Value
public class EvaluationResponseBatch {
    private final List<EvaluationResponse> items;

    public EvaluationResponseBatch(List<EvaluationResponse> items) {
        Objects.requireNonNull(items);
        this.items = Collections.unmodifiableList(items);
    }

    public Decision getDecision() {
        return items.stream()
                .map(EvaluationResponse::getDecision)
                .anyMatch(v -> !v.isPositive())
                ? Decision.Deny : Decision.Permit;
    }
}

