package com.rooxteam.sso.aal.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * hold all context for policy evaluation
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EvaluationContext {
    public static final String WEB_AGENT_SERVICE_NAME = "iPlanetAMWebAgentService";

    private String serviceName = WEB_AGENT_SERVICE_NAME;
    private String actionName;
    private String resourceName;
    private Map envParams;
    private String realm;

    public EvaluationContext(String realm, String resourceName, String actionName, Map envParams) {
        this.realm = realm;
        this.resourceName = resourceName;
        this.actionName = actionName;
        this.envParams = envParams;
    }
}
