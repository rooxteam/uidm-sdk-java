package com.rooxteam.sso.aal.client;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;
import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.PrincipalImpl;
import com.rooxteam.sso.aal.PropertyScope;
import com.rooxteam.sso.aal.exception.AuthenticationException;
import com.rooxteam.sso.aal.exception.AuthorizationException;
import com.sun.identity.authentication.AuthContext;
import com.sun.identity.authentication.spi.AuthLoginException;
import com.sun.identity.policy.ActionDecision;
import com.sun.identity.policy.PolicyDecision;
import com.sun.identity.policy.PolicyException;
import com.sun.identity.policy.client.PolicyEvaluator;
import com.sun.identity.policy.client.PolicyEvaluatorFactory;
import com.sun.identity.shared.locale.L10NMessageImpl;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.configuration.Configuration;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.NullNode;
import org.codehaus.jackson.node.ObjectNode;

import java.io.IOException;
import java.util.*;

import static com.rooxteam.sso.aal.AalLogger.LOG;

/**
 * @author Dmitry Tikhonov
 */
public class SsoAuthorizationClient {

    private ObjectMapper jsonMapper = new ObjectMapper();

    private static final String USER_ORGANIZATION_NAME = "customer";
    private static final String AUTHENTICATION_INDEX_NAME = "uidm";
    private static final String WEB_AGENT_SERVICE_NAME = "iPlanetAMWebAgentService";
    private static final String ALLOW_POLICY_DECISION = "allow";


    /**
     * RX REST policy service constants
     */
    private static final String PERMIT_POLICY_DECISION = "Permit";
    private static final String DENY_POLICY_DECISION = "Deny";

    private static final String IS_ALLOWED_PATH = "/api/policyEvaluation/isAllowed";
    private static final String TOKEN_INFO_PATH = "/oauth2/tokeninfo";
    private final Configuration config;
    private CloseableHttpClient httpClient;
    private JsonNode localPolicies = NullNode.getInstance();

    public SsoAuthorizationClient(Configuration rooxConfig, CloseableHttpClient httpClient) {
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

    public SSOToken authenticateByJwt(String jwt) {
        try {
            AuthContext authContext = initAuthContext();
            authContext.login(AuthContext.IndexType.SERVICE, AUTHENTICATION_INDEX_NAME, new String[]{jwt});
            if (authContext.getStatus().equals(AuthContext.Status.SUCCESS)) {
                return authContext.getSSOToken();
            } else {
                if (authContext.getLoginException() != null) {
                    LOG.errorLogin(authContext.getLoginException());
                } else {
                    LOG.errorUnexpectedStateAfterAuthenticationInSso(authContext.getStatus());
                }
            }
        } catch (AuthLoginException e) {
            LOG.errorAuthLogin(e);
        } catch (L10NMessageImpl l10NMessage) {
            LOG.errorI10n(l10NMessage);
        }

        return null;
    }

    protected AuthContext initAuthContext() throws AuthLoginException {
        return new AuthContext(USER_ORGANIZATION_NAME, httpClient);
    }

    public boolean isActionOnResourceAllowedByPolicy(SSOToken ssoToken, String resource, String method) {
        if (ssoToken == null) {
            LOG.warnNullSsoToken();
            return false;
        }

        if (resource == null || resource.length() == 0) {
            LOG.warnNullResource();
            return false;
        }

        if (method == null || method.length() == 0) {
            LOG.warnNullMethod();
            return false;
        }

        boolean result = false;
        try {
            PolicyEvaluator policyEvaluator = PolicyEvaluatorFactory.getInstance().getPolicyEvaluator(WEB_AGENT_SERVICE_NAME);
            PolicyDecision policyDecision = policyEvaluator.getPolicyDecision(ssoToken, resource, Collections.singleton(method));

            if (policyDecision.getActionDecisions().size() == 0) {
                result = config.getBoolean(ConfigKeys.ALLOW_ACCESS_WITHOUT_POLICY, ConfigKeys.ALLOW_ACCESS_WITHOUT_POLICY_DEFAULT);
            } else {
                String decision = getValueOfPolicyDecisionForMethod(method, policyDecision);
                result = decision.equals(ALLOW_POLICY_DECISION);
            }
        } catch (PolicyException e) {
            LOG.errorCreateEvaluator(WEB_AGENT_SERVICE_NAME, e);
        } catch (SSOException e) {
            LOG.errorSsoTokenInvalid(WEB_AGENT_SERVICE_NAME, e);
        }
        return result;
    }

    private String getValueOfPolicyDecisionForMethod(String method, PolicyDecision policyDecision) {
        ActionDecision actionDecisionForMethod = (ActionDecision) policyDecision.getActionDecisions().get(method);
        String decision = "";
        if (actionDecisionForMethod != null) {
            Set actionDecisionValues = actionDecisionForMethod.getValues();
            if (actionDecisionValues != null) {
                Object[] actionDecisions = actionDecisionValues.toArray();
                if (actionDecisions.length > 0) {
                    Object actionDecision = actionDecisions[0];
                    if (actionDecision != null) {
                        decision = actionDecision.toString();
                    }
                }
            }

        }
        return decision;
    }

    public void invalidateSSOSession(SSOToken ssoToken) {
        try {
            SSOTokenManager.getInstance().destroyToken(ssoToken);
        } catch (Exception e) {
            LOG.traceFailedToInvalidateSSOToken(e);
        }
    }

    /**
     * Token validation
     *
     * @param jwtToken Token value
     * @return True if token is valid
     */
    public Principal validate(final String jwtToken) {

        if (jwtToken == null) {
            LOG.warnNullSsoToken();
            return null;
        }

        try {
            String url = config.getString(ConfigKeys.SSO_URL) + TOKEN_INFO_PATH;
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("access_token", jwtToken));
            HttpPost post = HttpHelper.getHttpPost(url, params);
            HttpClientContext context = new HttpClientContext();
            CloseableHttpResponse response = httpClient.execute(post, context);

            int statusCode = response.getStatusLine().getStatusCode();
            Principal principal = null;

            if (statusCode == HttpStatus.SC_OK) {
                String responseJson = EntityUtils.toString(response.getEntity());
                JsonNode jsonNode = new ObjectMapper().readTree(responseJson);

                Map<String, Object> sharedIdentityProperties = new HashedMap();
                Object cn = jsonNode.get("cn").asText();
                sharedIdentityProperties.put("prn", cn);
                sharedIdentityProperties.put("sub", cn);
                sharedIdentityProperties.put("realm", jsonNode.get("realm").asText());

                List<String> authLevel = new ArrayList<>();
                authLevel.add(jsonNode.get("auth_level").asText());
                sharedIdentityProperties.put("authLevel", authLevel);
                Calendar expiresIn = new GregorianCalendar();
                expiresIn.set(Calendar.HOUR, 0);
                expiresIn.set(Calendar.MINUTE, jsonNode.get("expires_in").asInt());
                expiresIn.set(Calendar.SECOND, 0);
                principal = new PrincipalImpl(jwtToken, sharedIdentityProperties, expiresIn);
            }

            StringBuilder stringBuilder = new StringBuilder("Validation token code is ");
            stringBuilder.append(response.getStatusLine().getStatusCode());
            LOG.debug(stringBuilder.toString());
            return principal;
        } catch (IOException e) {
            LOG.errorAuthentication(e);
            throw new AuthorizationException("Failed to authorize because of communication or protocol error", e);
        } catch (Exception e) {
            LOG.errorAuthentication(e);
            throw e;
        }
    }

    public boolean isActionOnResourceAllowedByPolicy(String jwtToken, String resource, String method, Map env) {
        if (jwtToken == null) {
            LOG.warnNullSsoToken();
            return false;
        }

        if (resource == null || resource.length() == 0) {
            LOG.warnNullResource();
            return false;
        }

        if (method == null || method.length() == 0) {
            LOG.warnNullMethod();
            return false;
        }

        String realm = config.getString(ConfigKeys.REALM, ConfigKeys.REALM_DEFAULT);
        EvaluationContext evaluationContext = new EvaluationContext(realm, resource, method, env);

        try {
            String url = config.getString(ConfigKeys.SSO_URL) + IS_ALLOWED_PATH;
            HttpPost post = HttpHelper.getHttpPostWithJsonBody(url, jsonMapper.writeValueAsString(evaluationContext));
            post.addHeader("Authorization", "Bearer " + jwtToken);
            return doIsAllowedPost(post);
        } catch (IOException e) {
            LOG.errorAuthentication(e);
            throw new AuthorizationException("Failed to authorize because of communication or protocol error", e);
        } catch (Exception e) {
            LOG.errorAuthentication(e);
            throw e;
        }
    }

    private boolean doIsAllowedPost(HttpPost post) throws IOException {
        String result;

        HttpClientContext context = new HttpClientContext();
        context.setCookieStore(new BasicCookieStore());
        try (CloseableHttpResponse response = httpClient.execute(post, context)) {
            if (response.getStatusLine().getStatusCode() == 403) {
                return false;
            }
            if (response.getStatusLine().getStatusCode() == 401) {
                return false;
            }
            result = EntityUtils.toString(response.getEntity());
        }
        if (result == null) {
            throw new AuthenticationException("No or empty response from server");
        }

        ObjectNode jsonResult = null;
        try {
            jsonResult = (ObjectNode) jsonMapper.readTree(result);
        } catch (IOException e) {
            throw new AuthorizationException("Failed to read response from server", e);
        }
        if (jsonResult.has("error")) {
            //{"error_description":"Resource owner authentication failed","error":"invalid_grant"}
            JsonNode error = jsonResult.get("error");
            String errorCode = null;
            if (error.has("code")) {
                errorCode = error.get("code").asText();
            }
            String message = null;
            if (error.has("message")) {
                message = error.get("message").asText();
            }
            throw new AuthorizationException(message, message, errorCode);
        }
        if (jsonResult.has("decision")) {
            String decision = jsonResult.get("decision").asText();
            return decision != null && decision.equals(PERMIT_POLICY_DECISION);
        }

        throw new AuthorizationException("Response from server contains no decision and no error");
    }

    public boolean isActionOnResourceAllowedByConfigPolicy(Principal subject, String resourceName, String actionName) {

        Integer requiredLevel = getRequiredLevel(resourceName, actionName);

        if (requiredLevel != null && requiredLevel == 0) {
            // special handling for 0 level - allow unauthorized access
            return true;
        }

        List<String> authLevels = (List<String>) subject.getProperty(PropertyScope.SHARED_IDENTITY_PARAMS, "authLevel");
        if (authLevels == null || authLevels.isEmpty()) {
            // TODO: log
            return false;
        }
        int userAuthLevel = Integer.valueOf(authLevels.get(0));

        boolean result;
        if (requiredLevel == null) {
            result = config.getBoolean(ConfigKeys.ALLOW_ACCESS_WITHOUT_POLICY, ConfigKeys.ALLOW_ACCESS_WITHOUT_POLICY_DEFAULT);
        } else {
            result = userAuthLevel >= requiredLevel;
        }
        return result;
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
}
