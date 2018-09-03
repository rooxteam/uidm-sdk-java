package com.rooxteam.sso.aal.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

import static java.util.Optional.ofNullable;

/**
 * hold all context for policy evaluation
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EvaluationContext implements Serializable {
    public static final String WEB_AGENT_SERVICE_NAME = "iPlanetAMWebAgentService";

    private String serviceName = WEB_AGENT_SERVICE_NAME;
    private String actionName;
    private String resourceName;
    private Map envParams;
    private Map extraParams;
    private String realm;

    public EvaluationContext(String realm, String resourceName, String actionName, Map envParams) {
        this(null, actionName, resourceName, envParams, null, realm);
    }

    public EvaluationContext(String realm, String resourceName, String actionName, Map envParams, Map extraParams) {
        this(null, actionName, resourceName, envParams, extraParams, realm);
    }

    public EvaluationContext(String serviceName, String actionName, String resourceName, Map envParams, Map extraParams, String realm) {
        this.serviceName = ofNullable(serviceName).orElse(WEB_AGENT_SERVICE_NAME);
        this.actionName = actionName;
        this.resourceName = resourceName;
        this.envParams = envParams;
        this.extraParams = ofNullable(extraParams).orElse(envParams);
        this.realm = realm;
    }
}
