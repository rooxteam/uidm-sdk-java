package com.rooxteam.sso.aal.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serializable;
import java.util.Map;

/**
 * hold all context for policy evaluation
 */
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EvaluationContext implements Serializable {
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
