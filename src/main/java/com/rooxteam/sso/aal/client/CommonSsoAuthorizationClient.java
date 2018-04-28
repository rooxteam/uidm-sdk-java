package com.rooxteam.sso.aal.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.Principal;
import com.rooxteam.sso.aal.PrincipalImpl;
import com.rooxteam.sso.aal.exception.AuthorizationException;
import com.rooxteam.sso.aal.exception.ValidateException;
import lombok.SneakyThrows;
import org.apache.commons.configuration.Configuration;
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
import java.util.*;

import static com.rooxteam.sso.aal.AalLogger.LOG;

/**
 * @author Ivan Volynkin
 *         ivolynkin@roox.ru
 */
abstract public class CommonSsoAuthorizationClient implements SsoAuthorizationClient {
    private static final String TOKEN_INFO_PATH = "/oauth2/tokeninfo";
    private static ObjectMapper mapper = new ObjectMapper();
    protected final Configuration config;
    protected final CloseableHttpClient httpClient;

    protected CommonSsoAuthorizationClient(Configuration config, CloseableHttpClient httpClient) {
        this.config = config;
        this.httpClient = httpClient;
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

        String url = config.getString(ConfigKeys.SSO_URL) + TOKEN_INFO_PATH;
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("access_token", token));
        HttpPost post = HttpHelper.getHttpPost(url, params);
        post.setEntity(new StringEntity(mapper.writeValueAsString(getRequestContext(request)), ContentType.APPLICATION_JSON));
        HttpClientContext context = new HttpClientContext();
        try (CloseableHttpResponse response = httpClient.execute(post, context)) {
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
        } catch (RuntimeException e) {
            LOG.errorAuthentication(e);
            throw e;
        }
    }

    private Map<String, Object> getRequestContext(HttpServletRequest request) {
        return ImmutableMap.of(
                "headers", getRequestHeaders(request),
                "url", request.getRequestURI(),
                "httpMethod", request.getMethod(),
                "ip", request.getRemoteAddr()
        );
    }

    @SneakyThrows
    private Map<String, List<String>> getRequestHeaders(HttpServletRequest request) {
        Map<String, List<String>> headers = new HashMap<>();
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            ArrayList<String> list = new ArrayList<>();
            String name = names.nextElement();
            Enumeration<String> values = request.getHeaders(name);
            while (values.hasMoreElements()) {
                String value = values.nextElement();
                list.add(value);
            }
            headers.put(name, list);
        }
        return headers;
    }

    private static Map<String, Object> parseJson(String json) {
        try {
            return mapper.readValue(json, Map.class);
        } catch (IOException e) {
            throw new ValidateException("Failed to parse json", e);
        }
    }

}
