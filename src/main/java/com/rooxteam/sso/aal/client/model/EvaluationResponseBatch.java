package com.rooxteam.sso.aal.client.model;

import java.util.List;
import lombok.Value;

@Value
public class EvaluationResponseBatch {
    private List<EvaluationResponse> items;
}

