package com.rooxteam.sso.aal.client;

import com.rooxteam.sso.aal.*;
import com.rooxteam.sso.aal.client.model.EvaluationRequest;
import com.rooxteam.sso.aal.client.model.EvaluationResponse;
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
import org.codehaus.jackson.node.ObjectNode;

import java.io.IOException;
import java.util.*;

import static com.rooxteam.sso.aal.AalLogger.LOG;

/**
 * @author Dmitry Tikhonov
 */
public class SsoAuthorizationClientByJwt implements SsoAuthorizationClient {

    private final ObjectMapper jsonMapper = new ObjectMapper();

    private static final String IS_ALLOWED_PATH = "/api/policyEvaluation/isAllowed";
    private static final String WHICH_ALLOWED_PATH = "/api/policyEvaluation/whichAllowed";
    private static final String TOKEN_INFO_PATH = "/oauth2/tokeninfo";
    private final Configuration config;
    private CloseableHttpClient httpClient;

    public SsoAuthorizationClientByJwt(Configuration rooxConfig, CloseableHttpClient httpClient) {
        config = rooxConfig;
        this.httpClient = httpClient;
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
                Map<String, Object> tokenClaims = jsonMapper.readValue(responseJson, Map.class);
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

                Object realm = tokenClaims.get("realm");
                if (realm != null) {
                    sharedIdentityProperties.put("realm", realm.toString());
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
        String token = getJwtToken(subject);

        if (token == null) {
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

        String realm = (String) subject.getProperty(PropertyScope.SHARED_IDENTITY_PARAMS, "realm");
        if (realm == null) {
            realm = config.getString(ConfigKeys.REALM, ConfigKeys.REALM_DEFAULT);
        }

        EvaluationContext evaluationContext = new EvaluationContext(realm, resource, method, env);

        try {
            String url = config.getString(ConfigKeys.SSO_URL) + IS_ALLOWED_PATH;
            HttpPost post = HttpHelper.getHttpPostWithJsonBody(url, jsonMapper.writeValueAsString(evaluationContext));
            post.addHeader("Authorization", "Bearer " + token);
            return doIsAllowedPost(post);
        } catch (IOException e) {
            LOG.errorAuthentication(e);
            throw new AuthorizationException("Failed to authorize because of communication or protocol error", e);
        } catch (Exception e) {
            LOG.errorAuthentication(e);
            throw e;
        }
    }

    @Override
    public Map<EvaluationRequest, EvaluationResponse> whichActionAreAllowed(Principal subject, List<EvaluationRequest> policies) {
        String token = getJwtToken(subject);

        if (token == null) {
            LOG.warnNullSsoToken();
            throw new IllegalArgumentException("Authorization token is not supplied");
        }

        try {
            String realm = (String) subject.getProperty(PropertyScope.SHARED_IDENTITY_PARAMS, "realm");
            List<EvaluationContext> contexts = new ArrayList<>();
            for (EvaluationRequest policy : policies) {
                String method = policy.getActionName();
                String resource = policy.getResourceName();
                Map<String, ?> env = policy.getEnvParameters();
                contexts.add(new EvaluationContext(realm, resource, method, env));
            }

            String url = config.getString(ConfigKeys.SSO_URL) + WHICH_ALLOWED_PATH;
            HttpPost post = HttpHelper.getHttpPostWithJsonBody(url, jsonMapper.writeValueAsString(contexts));
            post.addHeader("Authorization", "Bearer " + token);
            EvaluationResponse[] responses = doWhichAllowedPost(post);

            if (policies.size() != responses.length) {
                throw new IllegalStateException("Wrong number of results");
            }

            Map<EvaluationRequest, EvaluationResponse> result = new HashMap<>();

            for (int i = 0; i < responses.length; i++) {
                result.put(policies.get(i), responses[i]);
            }

            return result;
        } catch (IOException e) {
            LOG.errorAuthentication(e);
            throw new AuthorizationException("Failed to authorize because of communication or protocol error", e);
        } catch (Exception e) {
            LOG.errorAuthentication(e);
            throw e;
        }
    }

    private String getJwtToken(Principal subject) {
        String jwt;
        if (subject instanceof PrincipalImpl) {
            jwt = ((PrincipalImpl) subject).getPrivateJwtToken();
        } else {
            jwt = subject.getJwtToken();
        }
        return jwt;
    }

    private EvaluationResponse doIsAllowedPost(HttpPost post) throws IOException {
        String result = executeRequest(post);

        return jsonMapper.readValue(result, EvaluationResponse.class);
    }

    private EvaluationResponse[] doWhichAllowedPost(HttpPost post) throws IOException {
        String result = executeRequest(post);
        return jsonMapper.readValue(result, EvaluationResponse[].class);
    }

    private String executeRequest(HttpPost post) throws IOException {
        HttpClientContext context = new HttpClientContext();
        context.setCookieStore(new BasicCookieStore());
        String result;
        try (CloseableHttpResponse response = httpClient.execute(post, context)) {
            result = EntityUtils.toString(response.getEntity());

            if (response.getStatusLine().getStatusCode() != 200) {
                try {
                    ObjectNode jsonResult = (ObjectNode) jsonMapper.readTree(result);
                    if (jsonResult.has("error")) {
                        processPolicyError(jsonResult);
                    } else {
                        // Proper json, without errors. Moving on.
                        return result;
                    }
                } catch (IOException e) {
                    throw new AuthorizationException("Failed to read a response from the server:" + response.getStatusLine(), e);
                }
            }
        }
        if (result == null) {
            throw new AuthorizationException("Empty response from the server");
        }

        return result;
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
}
