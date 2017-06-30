package com.rooxteam.sso.aal.client;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;
import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.PrincipalImpl;
import com.rooxteam.sso.aal.client.exception.NotSupportedException;
import com.rooxteam.sso.aal.client.model.Decision;
import com.rooxteam.sso.aal.client.model.EvaluationResponse;
import com.rooxteam.sso.aal.exception.AuthorizationException;
import com.rooxteam.sso.aal.utils.SsoPolicyDecisionUtils;
import com.sun.identity.authentication.AuthContext;
import com.sun.identity.authentication.spi.AuthLoginException;
import com.sun.identity.policy.PolicyDecision;
import com.sun.identity.policy.PolicyException;
import com.sun.identity.policy.client.PolicyEvaluator;
import com.sun.identity.policy.client.PolicyEvaluatorFactory;
import com.sun.identity.shared.locale.L10NMessageImpl;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
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
import org.forgerock.json.jose.utils.Utils;

import java.io.IOException;
import java.util.*;

import static com.rooxteam.sso.aal.AalLogger.LOG;

/**
 * @author Dmitry Tikhonov
 */
public class SsoAuthorizationClientByToken implements SsoAuthorizationClient {

    private final ObjectMapper jsonMapper = new ObjectMapper();

    private static final String USER_ORGANIZATION_NAME = "customer";
    private static final String AUTHENTICATION_INDEX_NAME = "uidm";
    private static final String WEB_AGENT_SERVICE_NAME = "iPlanetAMWebAgentService";

    private static final String TOKEN_INFO_PATH = "/oauth2/tokeninfo";
    private final Configuration config;
    private CloseableHttpClient httpClient;
    private JsonNode localPolicies = NullNode.getInstance();

    public SsoAuthorizationClientByToken(Configuration rooxConfig, CloseableHttpClient httpClient) {
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

    @Override
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
                    LOG.errorUnexpectedStateAfterAuthenticationInSso(authContext.getStatus().toString());
                }
            }
        } catch (AuthLoginException e) {
            LOG.errorAuthLogin(e);
        } catch (L10NMessageImpl l10NMessage) {
            LOG.errorI10n(l10NMessage);
        }

        return null;
    }

    public AuthContext initAuthContext() throws AuthLoginException {
        return new AuthContext(USER_ORGANIZATION_NAME, httpClient);
    }

    @Override
    public EvaluationResponse isActionOnResourceAllowedByPolicy(SSOToken ssoToken, String resource, String method) {

        if (ssoToken == null) {
            LOG.warnNullSsoToken();
            throw new IllegalArgumentException("Authorization token is not supplied");
        }

        if (StringUtils.isEmpty(resource)) {
            LOG.warnNullResource();
            throw new IllegalArgumentException("Resource name is not supplied");
        }

        if (StringUtils.isEmpty(method)) {
            LOG.warnNullMethod();
            throw new IllegalArgumentException("Method name is not supplied");
        }

        try {
            PolicyEvaluator policyEvaluator = PolicyEvaluatorFactory.getInstance().getPolicyEvaluator(WEB_AGENT_SERVICE_NAME);
            PolicyDecision policyDecision = policyEvaluator.getPolicyDecision(ssoToken, resource, Collections.singleton(method));

            if (policyDecision.getActionDecisions().isEmpty()) {
                boolean allow = config.getBoolean(ConfigKeys.ALLOW_ACCESS_WITHOUT_POLICY, ConfigKeys.ALLOW_ACCESS_WITHOUT_POLICY_DEFAULT);
                return new EvaluationResponse(Decision.fromAllow(allow), Collections.<String, String>emptyMap());
            } else {
                return SsoPolicyDecisionUtils.toEvaluationResponse(policyDecision, method);
            }
        } catch (PolicyException e) {
            LOG.errorCreateEvaluator(WEB_AGENT_SERVICE_NAME, e);
            throw new IllegalStateException("Unable to create SSO policy evaluator");
        } catch (SSOException e) {
            LOG.errorSsoTokenInvalid(WEB_AGENT_SERVICE_NAME, e);
            throw new IllegalArgumentException("Invalid SSO token (session expired?)");
        }
    }

    @Override
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
    @Override
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
                Map<String, Object> tokenClaims = Utils.parseJson(responseJson);
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
                principal = new PrincipalImpl(jwtToken, sharedIdentityProperties, expiresIn);
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
    public EvaluationResponse isActionOnResourceAllowedByPolicy(String jwtToken, String resource, String method, Map<String, ?> env) {
        throw new NotSupportedException();
    }

    @Override
    public EvaluationResponse isActionOnResourceAllowedByPolicy(Principal subject, String resourceName, String actionName) {
        throw new NotSupportedException();
    }
}
