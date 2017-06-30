package com.rooxteam.sso.aal.client;

import com.google.common.collect.ImmutableMap;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;
import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.PrincipalImpl;
import com.rooxteam.sso.aal.client.exception.NotSupportedException;
import com.rooxteam.sso.aal.client.model.Decision;
import com.rooxteam.sso.aal.client.model.EvaluationResponse;
import com.rooxteam.sso.aal.exception.AuthenticationException;
import com.rooxteam.sso.aal.exception.AuthorizationException;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
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
import org.forgerock.json.jose.utils.Utils;

import java.io.IOException;
import java.util.*;

import static com.rooxteam.sso.aal.AalLogger.LOG;

/**
 * @author Dmitry Tikhonov
 */
public class SsoAuthorizationClientByJwt implements SsoAuthorizationClient {

    private final ObjectMapper jsonMapper = new ObjectMapper();

    private static final String IS_ALLOWED_PATH = "/api/policyEvaluation/isAllowed";
    private static final String TOKEN_INFO_PATH = "/oauth2/tokeninfo";
    private final Configuration config;
    private CloseableHttpClient httpClient;
    private JsonNode localPolicies = NullNode.getInstance();

    public SsoAuthorizationClientByJwt(Configuration rooxConfig, CloseableHttpClient httpClient) {
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
    public EvaluationResponse isActionOnResourceAllowedByPolicy(SSOToken ssoToken, String resource, String method) {
        throw new NotSupportedException();
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
        if (jwtToken == null) {
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

    private EvaluationResponse doIsAllowedPost(HttpPost post) throws IOException {
        ObjectNode jsonResult = executeRequest(post);
        if (jsonResult.has("error")) {
            processPolicyError(jsonResult);
        }
        return parsePolicyDecision(jsonResult);
    }

    private ObjectNode executeRequest(HttpPost post) throws IOException {
        HttpClientContext context = new HttpClientContext();
        context.setCookieStore(new BasicCookieStore());
        String result;
        try (CloseableHttpResponse response = httpClient.execute(post, context)) {
            result = EntityUtils.toString(response.getEntity());
        }
        if (result == null) {
            throw new AuthenticationException("Empty response from the server");
        }

        ObjectNode jsonResult = null;
        try {
            jsonResult = (ObjectNode) jsonMapper.readTree(result);
        } catch (IOException e) {
            throw new AuthorizationException("Failed to read a response from the server", e);
        }
        return jsonResult;
    }

    private void processPolicyError(ObjectNode jsonResult) {
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

    private EvaluationResponse parsePolicyDecision(ObjectNode jsonResult) {
        if (!jsonResult.has("decision")) {
            throw new AuthorizationException("Response from server contains no decision and no error");
        }
        String decisionString = jsonResult.get("decision").asText();
        Decision decision = Decision.valueOf(decisionString);
        ImmutableMap.Builder<String, String> advicesResult = ImmutableMap.builder();
        if (jsonResult.has("advices")) {
            JsonNode advicesNode = jsonResult.get("advices");
            if (!advicesNode.isObject()) {
                throw new AuthorizationException("Unexpected advices format sent from the server");
            }
            ObjectNode advices = (ObjectNode) advicesNode;
            Iterator<Map.Entry<String, JsonNode>> fields = advices.getFields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                if (!field.getValue().isTextual()) {
                    LOG.errorInvalidAdviceContentType(field.getKey(), field.getValue().toString());
                    throw new AuthorizationException("Invalid advice content type");
                }
                advicesResult.put(field.getKey(), field.getValue().getTextValue());
            }
        }
        return new EvaluationResponse(decision, advicesResult.build());
    }

    @Override
    public EvaluationResponse isActionOnResourceAllowedByPolicy(Principal subject, String resourceName, String actionName) {
        throw new NotSupportedException();
    }

    @Override
    public SSOToken authenticateByJwt(String jwt) {
        throw new NotSupportedException();
    }

}
