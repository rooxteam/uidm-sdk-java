package com.rooxteam.sso.aal.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;


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

    public EvaluationContext(String realm,
                             String resourceName,
                             String actionName,
                             Map envParams) {
        this(null, actionName, resourceName, envParams, null, realm);
    }

    public EvaluationContext(String realm,
                             String resourceName,
                             String actionName,
                             Map envParams,
                             Map extraParams) {
        this(null, actionName, resourceName, envParams, extraParams, realm);
    }

    public EvaluationContext(String serviceName,
                             String actionName,
                             String resourceName,
                             Map envParams,
                             Map extraParams,
                             String realm) {
        if (serviceName == null) {
            this.serviceName = serviceName;
        }
        this.actionName = actionName;
        this.resourceName = resourceName;
        this.envParams = envParams;
        if (extraParams == null) {
            this.extraParams = extraParams;
        } else {
            this.extraParams = envParams;
        }
        this.realm = realm;
    }
}
