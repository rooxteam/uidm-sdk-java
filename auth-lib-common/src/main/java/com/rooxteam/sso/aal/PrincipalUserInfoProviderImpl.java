package com.rooxteam.sso.aal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.rooxteam.sso.aal.configuration.Configuration;
import com.rooxteam.sso.aal.exception.NetworkErrorException;
import com.rooxteam.sso.aal.exception.ValidateException;
import com.rooxteam.sso.aal.validation.impl.JwtTokenValidator;
import com.rooxteam.sso.aal.validation.jwt.ValidationResult;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.rooxteam.sso.aal.AalLogger.LOG;

@RequiredArgsConstructor
public class PrincipalUserInfoProviderImpl implements PrincipalProvider {

    private final Configuration config;
    private final CloseableHttpClient httpClient;
    private final JwtTokenValidator jwtTokenValidator;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public Principal getPrincipal(HttpServletRequest request, String accessToken) {
        if (accessToken == null) {
            LOG.warnNullSsoToken();
            return null;
        }

        String url = config.getString(ConfigKeys.USERINFO_URL);
        if (url == null) {
            LOG.warnNullUserInfoEndpoint();
            return null;
        }

        HttpPost post = new HttpPost(url);
        post.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        HttpClientContext context = new HttpClientContext();

        try (CloseableHttpResponse response = httpClient.execute(post, context)) {
            int statusCode = response.getCode();
            Principal principal = null;

            if (statusCode == HttpStatus.SC_OK) {
                JWT jwt = JWTParser.parse(accessToken);
                ValidationResult validationResult = jwtTokenValidator.validate(jwt);
                if (!validationResult.isSuccess()) {
                    return null;
                }

                String responseJson = EntityUtils.toString(response.getEntity());
                Map<String, Object> tokenClaims = parseJson(responseJson);

                Map<String, Object> properties = new HashMap<String, Object>();
                String subject = jwt.getJWTClaimsSet().getSubject();
                properties.put("sub", subject);

                for (Map.Entry<String, Object> entry : tokenClaims.entrySet()) {
                    properties.put(entry.getKey(), entry.getValue());
                }

                Calendar exp = Calendar.getInstance();

                principal = new PrincipalImpl(accessToken, properties, exp);
            }

            return principal;

        } catch (IOException e) {
            LOG.errorOnTokenValidationIO(url,
                    accessToken,
                    ConfigKeys.HTTP_CONNECTION_TIMEOUT,
                    config.getInt(ConfigKeys.HTTP_CONNECTION_TIMEOUT, ConfigKeys.HTTP_CONNECTION_TIMEOUT_DEFAULT),
                    ConfigKeys.HTTP_SOCKET_TIMEOUT,
                    config.getInt(ConfigKeys.HTTP_SOCKET_TIMEOUT, ConfigKeys.HTTP_SOCKET_TIMEOUT_DEFAULT),
                    e);
            throw new NetworkErrorException("Failed to validate token because of communication or protocol error", e);
        } catch (ParseException e) {
            LOG.errorOnParsingUserInfoResponse(url, e);
            return null;
        } catch (RuntimeException e) {
            LOG.errorOnTokenValidationGeneric(url, accessToken, e);
            throw e;
        } catch (org.apache.hc.core5.http.ParseException e) {
            LOG.errorOnTokenValidationGeneric(url, accessToken, e);
            return null;
        }
    }

    private static Map<String, Object> parseJson(String json) {
        try {
            return MAPPER.readValue(json, Map.class);
        } catch (IOException e) {
            throw new ValidateException("Failed to parse json", e);
        }
    }
}
