package com.rooxteam.sso.aal.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.PrincipalImpl;
import com.rooxteam.sso.aal.PropertyScope;
import com.rooxteam.sso.aal.client.model.EvaluationRequest;
import com.rooxteam.sso.aal.client.model.EvaluationResponse;
import com.rooxteam.sso.aal.configuration.Configuration;
import com.rooxteam.sso.aal.exception.AalException;
import com.rooxteam.sso.aal.exception.AuthorizationException;
import com.rooxteam.sso.aal.exception.NetworkErrorException;
import com.rooxteam.sso.aal.utils.StringUtils;
import com.rooxteam.util.HttpHelper;
import lombok.SneakyThrows;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rooxteam.sso.aal.AalLogger.LOG;

/**
 * @author Dmitry Tikhonov
 */
@SuppressWarnings("unused")
public class SsoAuthorizationClientByJwt extends CommonSsoAuthorizationClient {

    private final ObjectMapper jsonMapper;

    private static final String IS_ALLOWED_PATH = "/api/policyEvaluation/isAllowed";
    private static final String WHICH_ALLOWED_PATH = "/api/policyEvaluation/whichAllowed";
    private static final String TOKEN_INFO_PATH = "/oauth2/tokeninfo";

    public SsoAuthorizationClientByJwt(Configuration rooxConfig,
                                       CloseableHttpClient httpClient) {
        super(rooxConfig, httpClient);
        this.jsonMapper = new ObjectMapper();
        this.jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    @SneakyThrows
    public EvaluationResponse isActionOnResourceAllowedByPolicy(Principal subject,
                                                                String resource,
                                                                String method,
                                                                Map<String, ?> env) {
        String token = getJwtToken(subject);

        if (token == null) {
            LOG.warnNullSsoToken();
        }

        if (StringUtils.isEmpty(resource)) {
            LOG.warnNullResource();
            throw new IllegalArgumentException("Resource name is not supplied");
        }

        if (StringUtils.isEmpty(method)) {
            LOG.warnNullMethod();
            throw new IllegalArgumentException("Method name is not supplied");
        }

        String realm = (String) subject.getProperty("realm");
        if (realm == null) {
            realm = config.getString(ConfigKeys.REALM, ConfigKeys.REALM_DEFAULT);
        }

        ServletRequestAttributes requestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        EvaluationContext evaluationContext = requestAttributes != null
                ? new EvaluationContext(realm, resource, method, env, requestContextCollector.collect(requestAttributes.getRequest()))
                : new EvaluationContext(realm, resource, method, env);

        try {
            String url = config.getString(ConfigKeys.SSO_URL) + IS_ALLOWED_PATH;
            HttpPost post = HttpHelper.getHttpPostWithJsonBody(url, jsonMapper.writeValueAsString(evaluationContext));
            setupAuthorization(post, token);
            return doIsAllowedPost(post);
        } catch (IOException e) {
            LOG.errorAuthentication(e);
            throw new NetworkErrorException("Failed to authorize because of communication or protocol error", e);
        } catch (Exception e) {
            LOG.errorAuthentication(e);
            throw e;
        }
    }

    @Override
    @SneakyThrows
    public Map<EvaluationRequest, EvaluationResponse> whichActionAreAllowed(Principal subject,
                                                                            List<EvaluationRequest> policies) {
        String token = getJwtToken(subject);

        if (token == null) {
            LOG.warnNullSsoToken();
        }

        try {
            String realm = (String) subject.getProperty("realm");
            List<EvaluationContext> contexts = new ArrayList<EvaluationContext>();
            for (EvaluationRequest policy : policies) {
                String method = policy.getActionName();
                String resource = policy.getResourceName();
                Map<String, ?> env = policy.getEnvParameters();
                contexts.add(new EvaluationContext(realm, resource, method, env));
            }

            String url = config.getString(ConfigKeys.SSO_URL) + WHICH_ALLOWED_PATH;
            HttpPost post = HttpHelper.getHttpPostWithJsonBody(url, jsonMapper.writeValueAsString(contexts));
            setupAuthorization(post, token);
            EvaluationResponse[] responses = doWhichAllowedPost(post);

            if (policies.size() != responses.length) {
                throw new IllegalStateException("Wrong number of results");
            }

            Map<EvaluationRequest, EvaluationResponse> result = new HashMap<EvaluationRequest, EvaluationResponse>();

            for (int i = 0; i < responses.length; i++) {
                result.put(policies.get(i), responses[i]);
            }

            return result;
        } catch (IOException e) {
            LOG.errorAuthentication(e);
            throw new NetworkErrorException("Failed to authorize because of communication or protocol error", e);
        } catch (Exception e) {
            LOG.errorAuthentication(e);
            throw e;
        }
    }

    @Override
    public String postprocess(Principal subject, String resourceName, String actionName, Map<String, ?> envParameters, String response) {
        throw new AalException("postprocess is implemented in OPA mode only");
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

    private void setupAuthorization(HttpPost post, String token) {
        if (token != null) {
            post.addHeader("Authorization", "Bearer " + token);
        }
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
        CloseableHttpResponse response = httpClient.execute(post, context);
        try {
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
                    throw new NetworkErrorException("Failed to read a response from the server:" + response.getStatusLine(), e);
                }
            }
        } finally {
            response.close();
        }
        if (result == null) {
            throw new NetworkErrorException("Empty response from the server");
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
