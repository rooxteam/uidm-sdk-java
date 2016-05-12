package com.rooxteam.sso.aal.client;

import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.client.model.AuthenticationResponse;
import com.rooxteam.sso.aal.exception.AuthenticationException;
import com.rooxteam.sso.aal.exception.ErrorSubtypes;
import org.apache.commons.configuration.Configuration;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.rooxteam.sso.aal.AalLogger.LOG;

/**
 * HTTP клиент для аутентификации пользователей в SSO и обновлении информации о уже аутентифицированных пользователях.
 */
public class SsoAuthenticationClient {
    public static final String CLIENT_ID_PARAM_NAME = "client_id";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String GRANT_TYPE = "grant_type";
    public static final String REALM_PARAM_NAME = "realm";
    public static final String SERVICE_PARAM_NAME = "service";
    public static final String IP = "ip";
    public static final String GRANT_TYPE_M2M = "urn:roox:params:oauth:grant-type:m2m";

    public static final String JWT_PARAM_NAME = "jwt";
    public static final String UPDATE_LIFE_TIME_PARAM = "updateLifeTime";

    public final static List<String> COMMON_PARAMS = new ArrayList<>();


    private ObjectMapper jsonMapper = new ObjectMapper();


    private Configuration config;
    private CloseableHttpClient httpClient;

    public SsoAuthenticationClient(Configuration config, CloseableHttpClient httpClient) {
        this.config = config;
        this.httpClient = httpClient;

        COMMON_PARAMS.add(CLIENT_ID_PARAM_NAME);
        COMMON_PARAMS.add(CLIENT_SECRET);
        COMMON_PARAMS.add(GRANT_TYPE);
        COMMON_PARAMS.add(REALM_PARAM_NAME);
        COMMON_PARAMS.add(SERVICE_PARAM_NAME);

        this.jsonMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Authenticate by all parameter (auto). Sso should decide which method to choose.
     *
     * @param params authentication params
     * @return JWT token or throws AuthenticationException
     */
    public AuthenticationResponse authenticate(Map<String, ?> params) {
        List<NameValuePair> commonParams = getCommonNameValuePairs();

        for (Map.Entry<String, ?> entry : params.entrySet()) {
            String key = entry.getKey();
            if (COMMON_PARAMS.contains(key)) {
                LOG.warnSkippingCommonParamInAuthRequest(key);
                continue;
            }
            if (entry.getValue() != null) {
                commonParams.add(new BasicNameValuePair(key, entry.getValue().toString()));
            }
        }

        try {
            HttpPost post = getHttpPostWithEntity(commonParams);
            return doAuthenticationPost(post);
        } catch (IOException e) {
            LOG.errorAuthentication(e);
            throw new AuthenticationException("Failed to authenticate because of communication or protocol error", e);
        } catch (AuthenticationException e) {
            if (e.getErrorSubtype() != null && e.getErrorSubtype().equals(ErrorSubtypes.IP_NOT_IN_POOL)) {
                LOG.traceIpNotInPool();
            } else {
                LOG.errorAuthentication(e);
            }
            throw e;
        } catch (Exception e) {
            LOG.errorAuthentication(e);
            throw e;
        }
    }


    private List<NameValuePair> getCommonNameValuePairs() {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(CLIENT_ID_PARAM_NAME, config.getString(ConfigKeys.CLIENT_ID)));
        params.add(new BasicNameValuePair(CLIENT_SECRET, config.getString(ConfigKeys.CLIENT_SECRET)));
        params.add(new BasicNameValuePair(GRANT_TYPE, GRANT_TYPE_M2M));
        params.add(new BasicNameValuePair(REALM_PARAM_NAME, config.getString(ConfigKeys.REALM, ConfigKeys.REALM_DEFAULT)));
        params.add(new BasicNameValuePair(SERVICE_PARAM_NAME, config.getString(ConfigKeys.AUTH_SERVICE, ConfigKeys.AUTH_SERVICE_DEFAULT)));
        return params;
    }

    private HttpPost getHttpPostWithEntity(List<NameValuePair> params) throws UnsupportedEncodingException {
        String url = config.getString(ConfigKeys.SSO_URL) + OtpClient.OAUTH2_ACCESS_TOKEN_PATH;
        return HttpHelper.getHttpPostWithEntity(url, params);
    }

    private AuthenticationResponse doAuthenticationPost(HttpPost post) throws IOException {
        String result;

        HttpClientContext context = new HttpClientContext();
        context.setCookieStore(new BasicCookieStore());
        try (CloseableHttpResponse response = httpClient.execute(post, context)) {
            result = EntityUtils.toString(response.getEntity());
        }
        if (result == null) {
            throw new AuthenticationException("No or empty response from server");
        }

        ObjectNode jsonResult = null;
        try {
            jsonResult = (ObjectNode) jsonMapper.readTree(result);
        } catch (IOException e) {
            throw new AuthenticationException("Failed to read response from server", e);
        }
        if (!jsonResult.has("error")) {
            return jsonMapper.readValue(jsonResult,AuthenticationResponse.class);
        }
        if (jsonResult.has("error")) {
            //{"error_description":"Resource owner authentication failed","error":"invalid_grant"}
            String error = jsonResult.get("error").asText();
            String errorDescription = null;
            if (jsonResult.has("error_description")) {
                errorDescription = jsonResult.get("error_description").asText();
            }
            String errorSubtype = null;
            if (jsonResult.has("error_subtype")) {
                errorSubtype = jsonResult.get("error_subtype").asText();
            }
            throw new AuthenticationException(error, errorDescription, errorSubtype);
        }

        throw new AuthenticationException("Response from server contains no token and no error");
    }

}
