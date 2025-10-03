package com.rooxteam.sso.aal.client.model;

import java.util.Collections;
import java.util.List;

import com.rooxteam.compat.Objects;
import lombok.Value;

@Value
public class EvaluationResponseBatch {
    private final List<EvaluationResponse> items;

    public EvaluationResponseBatch(List<EvaluationResponse> items) {
        Objects.requireNonNull(items);
        this.items = Collections.unmodifiableList(items);
    }

    public Decision getDecision() {
        for (EvaluationResponse item : items) {
            if(!item.getDecision().isPositive()){
                return Decision.Deny;
            }
        }
        return Decision.Permit;
    }
}

