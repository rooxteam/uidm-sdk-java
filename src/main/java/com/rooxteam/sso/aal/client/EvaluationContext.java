package com.rooxteam.sso.aal.client;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * hold all context for policy evaluation
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EvaluationContext {
    public static final String WEB_AGENT_SERVICE_NAME = "iPlanetAMWebAgentService";

    private String serviceName = WEB_AGENT_SERVICE_NAME;
    private String actionName = "GET";
    private String resourceName;
    private Map envParams = new HashMap();
    private String realm;

    public EvaluationContext(String realm, String resourceName, String actionName, Map envParams) {
        this.realm = realm;
        this.resourceName = resourceName;
        this.actionName = actionName;
        this.envParams = envParams;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getActionName() {
        return actionName;
    }

    public String getResourceName() {
        return resourceName;
    }

    public Map getEnvParams() {
        return envParams;
    }

    public String getRealm() {
        return realm;
    }
}
