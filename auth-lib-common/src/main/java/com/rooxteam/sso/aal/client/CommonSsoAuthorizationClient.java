package com.rooxteam.sso.aal.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.PrincipalImpl;
import com.rooxteam.sso.aal.configuration.Configuration;
import com.rooxteam.sso.aal.exception.NetworkErrorException;
import com.rooxteam.sso.aal.exception.ValidateException;
import com.rooxteam.sso.aal.userIp.UserIpProviderFactory;
import lombok.SneakyThrows;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rooxteam.sso.aal.AalLogger.LOG;

/**
 * @author Ivan Volynkin
 * ivolynkin@roox.ru
 */
abstract public class CommonSsoAuthorizationClient implements SsoAuthorizationClient {
    private static final String TOKEN_INFO_PATH = "/oauth2/tokeninfo";
    private static ObjectMapper mapper = new ObjectMapper();
    protected final Configuration config;
    protected final CloseableHttpClient httpClient;
    protected final RequestContextCollector requestContextCollector;

    protected CommonSsoAuthorizationClient(Configuration config, CloseableHttpClient httpClient) {
        this(config, httpClient, new RequestContextCollector(new UserIpProviderFactory(config).create()));
    }

    protected CommonSsoAuthorizationClient(Configuration config, CloseableHttpClient httpClient, RequestContextCollector requestContextCollector) {
        this.config = config;
        this.httpClient = httpClient;
        this.requestContextCollector = requestContextCollector;
    }

    /**
     * Token validation
     *
     * @param token Token value
     * @return True if token is valid
     */
    @SneakyThrows({JsonProcessingException.class})
    @Override
    public Principal validate(HttpServletRequest request, final String token) {
        if (token == null) {
            LOG.warnNullSsoToken();
            return null;
        }

        final String tokenForLogging = trimTokenForLogging(token);

        String url = config.getString(ConfigKeys.SSO_URL) + TOKEN_INFO_PATH;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("access_token", token));
        HttpPost post = HttpHelper.getHttpPost(url, params);
        post.setEntity(new StringEntity(mapper.writeValueAsString(requestContextCollector.collect(request)), ContentType.APPLICATION_JSON));
        HttpClientContext context = new HttpClientContext();
        try {
            CloseableHttpResponse response = httpClient.execute(post, context);
            try {
                int statusCode = response.getStatusLine().getStatusCode();
                Principal principal = null;

                if (statusCode == HttpStatus.SC_OK) {
                    String responseJson = EntityUtils.toString(response.getEntity());
                    Map<String, Object> tokenClaims = parseJson(responseJson);
                    Map<String, Object> sharedIdentityProperties = new HashMap<String, Object>();
                    Object sub = tokenClaims.get("sub");
                    // legacy style claim for subject
                    sharedIdentityProperties.put("prn", sub);
                    for (Map.Entry<String, Object> entry : tokenClaims.entrySet()) {
                        sharedIdentityProperties.put(entry.getKey(),entry.getValue());
                    }

                    // this is for backward compat because by some legacy nonsense authLevel has been defined as list
                    Object authLevel = tokenClaims.get("auth_level");
                    if (authLevel != null) {
                        sharedIdentityProperties.put("authLevel", Collections.singletonList(authLevel.toString()));
                    } else {
                        sharedIdentityProperties.put("authLevel", Collections.emptyList());
                    }

                    Calendar expiresIn = new GregorianCalendar();
                    expiresIn.set(Calendar.HOUR, 0);
                    expiresIn.set(Calendar.MINUTE, Integer.valueOf(tokenClaims.get("expires_in").toString()));
                    expiresIn.set(Calendar.SECOND, 0);
                    principal = new PrincipalImpl(token, sharedIdentityProperties, expiresIn);
                }
                return principal;
            } finally {
                response.close();
            }
        } catch (IOException e) {
            LOG.errorOnTokenValidationIO(url,
                    tokenForLogging,
                    ConfigKeys.HTTP_CONNECTION_TIMEOUT,
                    config.getInt(ConfigKeys.HTTP_CONNECTION_TIMEOUT, ConfigKeys.HTTP_CONNECTION_TIMEOUT_DEFAULT),
                    ConfigKeys.HTTP_SOCKET_TIMEOUT,
                    config.getInt(ConfigKeys.HTTP_SOCKET_TIMEOUT, ConfigKeys.HTTP_SOCKET_TIMEOUT_DEFAULT),
                    e);
            throw new NetworkErrorException("Failed to validate token because of communication or protocol error", e);
        } catch (RuntimeException e) {
            LOG.errorOnTokenValidationGeneric(url,
                    tokenForLogging,
                    e);
            throw e;
        }
    }

    private static Map<String, Object> parseJson(String json) {
        try {
            return mapper.readValue(json, Map.class);
        } catch (IOException e) {
            throw new ValidateException("Failed to parse json", e);
        }
    }

    private String trimTokenForLogging(String token) {
        if(token!=null){
            return token.substring(0, Math.min(16, token.length()));
        }else{
            return "<none>";
        }
    }


}
