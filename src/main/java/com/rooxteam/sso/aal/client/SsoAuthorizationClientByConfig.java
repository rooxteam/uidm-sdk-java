package com.rooxteam.sso.aal.client;

import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.PrincipalImpl;
import com.rooxteam.sso.aal.PropertyScope;
import com.rooxteam.sso.aal.client.model.Decision;
import com.rooxteam.sso.aal.client.model.EvaluationResponse;
import com.rooxteam.sso.aal.exception.AuthorizationException;
import com.rooxteam.sso.aal.exception.ValidateException;
import org.apache.commons.configuration.Configuration;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.NullNode;

import java.io.IOException;
import java.util.*;

import static com.rooxteam.sso.aal.AalLogger.LOG;

/**
 * @author Dmitry Tikhonov
 */
public class SsoAuthorizationClientByConfig implements SsoAuthorizationClient {

    private final ObjectMapper jsonMapper = new ObjectMapper();

    private static final String TOKEN_INFO_PATH = "/oauth2/tokeninfo";
    private final Configuration config;
    private CloseableHttpClient httpClient;
    private JsonNode localPolicies = NullNode.getInstance();
    private static ObjectMapper mapper = new ObjectMapper();

    public SsoAuthorizationClientByConfig(Configuration rooxConfig, CloseableHttpClient httpClient) {
        config = rooxConfig;
        this.httpClient = httpClient;
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

    /**
     * Token validation
     *
     * @param token Token value
     * @return True if token is valid
     */
    @Override
    public Principal validate(final String token) {

        if (token == null) {
            LOG.warnNullSsoToken();
            return null;
        }

        try {
            String url = config.getString(ConfigKeys.SSO_URL) + TOKEN_INFO_PATH;
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("access_token", token));
            HttpPost post = HttpHelper.getHttpPost(url, params);
            HttpClientContext context = new HttpClientContext();
            CloseableHttpResponse response = httpClient.execute(post, context);

            int statusCode = response.getStatusLine().getStatusCode();
            Principal principal = null;

            if (statusCode == HttpStatus.SC_OK) {
                String responseJson = EntityUtils.toString(response.getEntity());
                Map<String, Object> tokenClaims = parseJson(responseJson);
                Map<String, Object> sharedIdentityProperties = new HashMap<>();
                Object cn = tokenClaims.get("sub");
                sharedIdentityProperties.put("prn", cn);
                sharedIdentityProperties.put("sub", cn);
                String[] toForward = config.getStringArray(ConfigKeys.TOKEN_INFO_ATTRIBUTES_FORWARD);
                for (String attr : toForward) {
                    if (tokenClaims.containsKey(attr)) {
                        sharedIdentityProperties.put(attr, tokenClaims.get(attr));
                    }
                }

                Object authLevel = tokenClaims.get("auth_level");
                if (authLevel != null) {
                    sharedIdentityProperties.put("authLevel", Collections.singletonList(authLevel.toString()));
                } else {
                    sharedIdentityProperties.put("authLevel", Collections.emptyList());
                }

                List<String> roles = (List<String>) tokenClaims.get("roles");
                if (roles != null) {
                    sharedIdentityProperties.put("roles", roles);
                }

                Calendar expiresIn = new GregorianCalendar();
                expiresIn.set(Calendar.HOUR, 0);
                expiresIn.set(Calendar.MINUTE, Integer.valueOf(tokenClaims.get("expires_in").toString()));
                expiresIn.set(Calendar.SECOND, 0);
                principal = new PrincipalImpl(token, sharedIdentityProperties, expiresIn);
            }
            return principal;
        } catch (IOException e) {
            LOG.errorAuthentication(e);
            throw new AuthorizationException("Failed to authorize because of communication or protocol error", e);
        } catch (Exception e) {
            LOG.errorAuthentication(e);
            throw e;
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

    private Integer getRequiredLevel(String resourceName, String actionName) {
        JsonNode policyAuthLevelNode = getAuthLevelFromLocalPolicies(resourceName, actionName);
        if (policyAuthLevelNode != null) {
            return policyAuthLevelNode.getIntValue();
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

    private static Map<String, Object> parseJson(String json) {
        try {
            return mapper.readValue(json, Map.class);
        } catch (IOException e) {
            throw new ValidateException("Failed to parse json", e);
        }
    }

}
