package com.rooxteam.sso.aal.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * hold all context for policy evaluation
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EvaluationContextBatch implements Serializable {
    public static final String WEB_AGENT_SERVICE_NAME = "iPlanetAMWebAgentService";

    private String serviceName = WEB_AGENT_SERVICE_NAME;
    private String realm;
    private List<EvaluationContext> items;

}
