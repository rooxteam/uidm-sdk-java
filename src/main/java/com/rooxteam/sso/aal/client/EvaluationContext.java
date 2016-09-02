package com.rooxteam.sso.aal.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * hold all context for policy evaluation
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EvaluationContext {
    public static final String WEB_AGENT_SERVICE_NAME = "iPlanetAMWebAgentService";

    private final String serviceName = WEB_AGENT_SERVICE_NAME;
    private final String actionName;
    private final String resourceName;
    private final Map envParams;
    private final String realm;

    public EvaluationContext(String realm, String resourceName, String actionName, Map envParams) {
        this.realm = realm;
        this.resourceName = resourceName;
        this.actionName = actionName;
        this.envParams = envParams;
    }
}
