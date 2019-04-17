package com.rooxteam.sso.aal;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
class PolicyDecisionKey implements AalCacheKey {

    private final Principal subject;
    private final String resourceName;
    private final String actionName;
    private final Map<String, ?> envParameters;

    public PolicyDecisionKey(Principal subject, String resourceName, String actionName) {
        this.subject = subject;
        this.resourceName = resourceName;
        this.actionName = actionName;
        this.envParameters = new HashMap<>();
    }
}
