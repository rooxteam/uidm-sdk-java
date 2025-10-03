package com.rooxteam.sso.aal.client.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationRequest {
    private String resourceName;
    private String actionName;
    private Map<String, ?> envParameters;
}
