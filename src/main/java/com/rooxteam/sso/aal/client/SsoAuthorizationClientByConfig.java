package com.rooxteam.sso.aal.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.PropertyScope;
import com.rooxteam.sso.aal.client.model.Decision;
import com.rooxteam.sso.aal.client.model.EvaluationRequest;
import com.rooxteam.sso.aal.client.model.EvaluationResponse;
import com.rooxteam.sso.aal.exception.AuthorizationException;
import org.apache.commons.configuration.Configuration;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dmitry Tikhonov
 */
public class SsoAuthorizationClientByConfig extends CommonSsoAuthorizationClient {

    private final ObjectMapper jsonMapper = new ObjectMapper();

    private JsonNode localPolicies = NullNode.getInstance();

    public SsoAuthorizationClientByConfig(Configuration rooxConfig, CloseableHttpClient httpClient) {
        super(rooxConfig, httpClient);
        initPolicies();
    }

    private void initPolicies() {
        String policiesStr = config.getString(ConfigKeys.LOCAL_POLICIES);
        try {
            if (policiesStr != null) {
                localPolicies = jsonMapper.readTree(policiesStr);
            }
        } catch (IOException e) {
            throw new AuthorizationException("Failed to read config property '" + ConfigKeys.LOCAL_POLICIES + "'", e);
        }
    }

    @Override
    public EvaluationResponse isActionOnResourceAllowedByPolicy(Principal subject, String resource, String method, Map<String, ?> env) {

        Integer requiredLevel = getRequiredLevel(resource, method);

        if (requiredLevel != null && requiredLevel == 0) {
            // special handling for 0 level - allow unauthorized access
            return new EvaluationResponse(Decision.Permit);
        }

        int userAuthLevel;

        if (subject.isAnonymous()) {
            userAuthLevel = 0;
        } else {
            List<String> authLevels = (List<String>) subject.getProperty(PropertyScope.SHARED_IDENTITY_PARAMS, "authLevel");
            if (authLevels == null || authLevels.isEmpty()) {
                userAuthLevel = 0;
            } else {
                userAuthLevel = Integer.valueOf(authLevels.get(0));
            }
        }

        boolean result;
        if (requiredLevel == null) {
            result = config.getBoolean(ConfigKeys.ALLOW_ACCESS_WITHOUT_POLICY, ConfigKeys.ALLOW_ACCESS_WITHOUT_POLICY_DEFAULT);
        } else {
            result = userAuthLevel >= requiredLevel;
        }
        return new EvaluationResponse(Decision.fromAllow(result));
    }

    @Override
    public Map<EvaluationRequest, EvaluationResponse> whichActionAreAllowed(Principal subject, List<EvaluationRequest> policies) {
        Map<EvaluationRequest, EvaluationResponse> result = new LinkedHashMap<>();

        for (EvaluationRequest decisionKey : policies) {
            result.put(decisionKey, isActionOnResourceAllowedByPolicy(subject, decisionKey.getResourceName(),
                    decisionKey.getActionName(), decisionKey.getEnvParameters()));
        }

        return result;
    }

    private Integer getRequiredLevel(String resourceName, String actionName) {
        JsonNode policyAuthLevelNode = getAuthLevelFromLocalPolicies(resourceName, actionName);
        if (policyAuthLevelNode != null && policyAuthLevelNode.isInt()) {
            return policyAuthLevelNode.asInt();
        } else {
            return null;
        }
    }

    private JsonNode getAuthLevelFromLocalPolicies(String resourceName, String actionName) {
        JsonNode resourcePolicies = localPolicies.get(resourceName);
        JsonNode policyAuthLevelNode = null;
        if (resourcePolicies != null) {
            JsonNode actionPolicy = resourcePolicies.get(actionName);
            if (actionPolicy != null) {
                policyAuthLevelNode = actionPolicy.get("authLevel");
            }
        }
        return policyAuthLevelNode;
    }

}
