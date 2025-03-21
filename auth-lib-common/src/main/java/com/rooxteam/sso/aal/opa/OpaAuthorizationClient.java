package com.rooxteam.sso.aal.opa;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.PrincipalImpl;
import com.rooxteam.sso.aal.client.CommonSsoAuthorizationClient;
import com.rooxteam.sso.aal.client.EvaluationContext;
import com.rooxteam.sso.aal.client.model.Decision;
import com.rooxteam.sso.aal.client.model.EvaluationRequest;
import com.rooxteam.sso.aal.client.model.EvaluationResponse;
import com.rooxteam.sso.aal.configuration.Configuration;
import com.rooxteam.sso.aal.exception.AalException;
import com.rooxteam.sso.aal.exception.AuthorizationException;
import com.rooxteam.sso.aal.exception.NetworkErrorException;
import com.rooxteam.sso.aal.utils.StringUtils;
import com.rooxteam.util.HttpHelper;
import lombok.SneakyThrows;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import static com.rooxteam.sso.aal.AalLogger.LOG;

/**
 * Authorize operations using Open Policy Agent
 *
 * @author RooX
 */
@SuppressWarnings("unused")
public class OpaAuthorizationClient extends CommonSsoAuthorizationClient {

    private final ObjectMapper jsonMapper;

    /**
     * This fits into Repo package (until last slash) and a policy name (after last slash)
     */
    private static final String IS_ALLOWED_POLICY = "/{0}/isAllowed";

    private static final String POSTPROCESS_POLICY = "/{0}/postprocess";

    public OpaAuthorizationClient(Configuration rooxConfig,
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
        final String accessToken = getAccessToken(subject);

        if (accessToken == null) {
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

        OpaPolicyRequest opaPolicyRequest = new OpaPolicyRequest(
                new OpaInput(
                        evaluationContext,
                        new OpaAuthorization(accessToken),
                        null
                )
        );

        String opaPackage = config.getString(ConfigKeys.OPA_PACKAGE, ConfigKeys.OPA_PACKAGE_DEFAULT);

        try {
            final String url = config.getString(ConfigKeys.OPA_DATA_API_URL) + new MessageFormat(IS_ALLOWED_POLICY).format(new String[]{opaPackage});
            final HttpPost post = HttpHelper.getHttpPostWithJsonBody(url, jsonMapper.writeValueAsString(opaPolicyRequest));
            String result = executeRequest(post);
            final OpaPolicyResponse opaPolicyResponse = jsonMapper.readValue(result, OpaPolicyResponse.class);
            if (opaPolicyResponse.getResult() == null) {
                return new EvaluationResponse(Decision.Deny);
            }
            return opaPolicyResponse.getResult();
        } catch (IOException e) {
            LOG.errorAuthentication(e);
            throw new NetworkErrorException("Failed to authorize because of communication or protocol error", e);
        } catch (Exception e) {
            LOG.errorAuthentication(e);
            throw e;
        }
    }

    @Override
    public Map<EvaluationRequest, EvaluationResponse> whichActionAreAllowed(Principal subject, List<EvaluationRequest> policies) {
        throw new AalException("OPA implements isAllowed only");
    }

    @Override
    public String postprocess(Principal subject, String resource, String method, Map<String, ?> env, String response) {
        final String accessToken = getAccessToken(subject);

        if (accessToken == null) {
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

        OpaPolicyRequest opaPolicyRequest = new OpaPolicyRequest(
                new OpaInput(
                        evaluationContext,
                        new OpaAuthorization(accessToken),
                        response
                )
        );

        String opaPackage = config.getString(ConfigKeys.OPA_PACKAGE, ConfigKeys.OPA_PACKAGE_DEFAULT);

        try {
            final String url = config.getString(ConfigKeys.OPA_DATA_API_URL) + new MessageFormat(POSTPROCESS_POLICY).format(new String[]{opaPackage});
            final HttpPost post = HttpHelper.getHttpPostWithJsonBody(url, jsonMapper.writeValueAsString(opaPolicyRequest));
            String result = executeRequest(post);
            return jsonMapper.writeValueAsString(jsonMapper.readValue(result, OpaPostprocessResponse.class).getResult());
        } catch (IOException e) {
            LOG.errorAuthentication(e);
            throw new NetworkErrorException("Failed to authorize because of communication or protocol error", e);
        } catch (Exception e) {
            LOG.errorAuthentication(e);
            throw new AuthorizationException(e);
        }
    }

    private String getAccessToken(Principal subject) {
        String jwt;
        if (subject instanceof PrincipalImpl) {
            jwt = ((PrincipalImpl) subject).getPrivateJwtToken();
        } else {
            jwt = subject.getJwtToken();
        }
        return jwt;
    }

    private String executeRequest(HttpPost post) throws IOException {
        HttpClientContext context = new HttpClientContext();
        context.setCookieStore(new BasicCookieStore());
        String result;
        CloseableHttpResponse response = httpClient.execute(post, context);
        try {
            result = EntityUtils.toString(response.getEntity());

            if (response.getCode() != 200) {
                try {
                    ObjectNode jsonResult = (ObjectNode) jsonMapper.readTree(result);
                    if (jsonResult.has("error")) {
                        processPolicyError(jsonResult);
                    } else {
                        // Proper json, without errors. Moving on.
                        return result;
                    }
                } catch (IOException e) {
                    throw new NetworkErrorException("Failed to read a response from the server:" + response.getCode(), e);
                }
            }
        } catch (ParseException e) {
            throw new NetworkErrorException("Empty response from the server");
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
