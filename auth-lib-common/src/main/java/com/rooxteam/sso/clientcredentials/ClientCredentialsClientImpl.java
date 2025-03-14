package com.rooxteam.sso.clientcredentials;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.rooxteam.compat.Objects;
import com.rooxteam.sso.aal.ConfigKeys;
import com.rooxteam.sso.aal.exception.AuthenticationException;
import com.rooxteam.sso.aal.exception.NetworkErrorException;
import com.rooxteam.sso.aal.exception.ValidateException;
import com.rooxteam.sso.clientcredentials.configuration.Configuration;
import com.rooxteam.util.HttpHelper;
import com.rooxteam.util.TokenUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.rooxteam.sso.clientcredentials.ClientCredentialsClientLogger.LOG;


final class ClientCredentialsClientImpl implements ClientCredentialsClient {
    private final CloseableHttpClient httpClient;
    private final URI accessTokenEndpoint;
    private final URI tokenValidationEndpoint;
    private final Map<String, List<String>> defaultParameters;
    private final String headerPrefix;
    private final Configuration configuration;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final ConcurrentHashMap<Map<String, List<String>>, ClientCredentialsTokenModel> tokens = new ConcurrentHashMap<>();

    ClientCredentialsClientImpl(final CloseableHttpClient httpClient,
                                final URI accessTokenEndpoint,
                                final URI tokenValidationEndpoint,
                                final Map<String, List<String>> defaultParameters,
                                final String headerPrefix,
                                final Configuration configuration) {
        this.httpClient = Objects.requireNonNull(httpClient, "httpClient");
        this.accessTokenEndpoint = Objects.requireNonNull(accessTokenEndpoint, "accessTokenEndpoint");
        this.tokenValidationEndpoint = Objects.requireNonNull(tokenValidationEndpoint, "tokenValidationEndpoint");
        if (defaultParameters != null) {
            this.defaultParameters = defaultParameters;
        } else {
            this.defaultParameters = new LinkedHashMap<>();
        }
        this.headerPrefix = headerPrefix;
        this.configuration = configuration;
    }

    @Override
    public String getAuthHeaderValue(Map<String, List<String>> params) {
        String token = getTokenValidating(params);
        return headerPrefix + token;
    }

    @Override
    public String getToken(Map<String, List<String>> params) {
        return getTokenValidating(params);
    }

    @Override
    public String getAuthHeaderValue() {
        String token = getTokenValidating(defaultParameters);
        return headerPrefix + token;
    }

    @Override
    public String getToken() {
        return getTokenValidating(defaultParameters);
    }

    private boolean isExpired(String token) {
        if (token == null || token.isEmpty()) {
            return true;
        }
        final String tokenForLogging = trimTokenForLogging(token);
        try {
            switch (configuration.getValidationType()) {
                case USERINFO: {
                    HttpClientContext context = new HttpClientContext();
                    HttpPost httpPost = new HttpPost(tokenValidationEndpoint);
                    httpPost.setHeader(HttpHeaders.AUTHORIZATION, TokenUtils.wrapBearerToken(token));
                    httpPost.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON);
                    httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON);

                    return isTokenExpired(httpPost, context, tokenForLogging);
                }

                case JWT:
                    JWT jwt = JWTParser.parse(token);
                    Date exp = jwt.getJWTClaimsSet().getExpirationTime();
                    if (exp != null) {
                        Date now = new Date(Clock.systemUTC().millis());
                        LOG.traceExpAndCurrentTime(exp, now);
                        return now.after(exp);
                    } else {
                        LOG.traceMessage("No exp. Skipping check");
                    }
                    return false;

                case NONE:
                    return false;

                case TOKENINFO: {
                    HttpClientContext context = new HttpClientContext();

                    URI uri = new URIBuilder(tokenValidationEndpoint)
                            .addParameter("access_token", token)
                            .build();

                    return isTokenExpired(new HttpGet(uri), context, tokenForLogging);
                }

            }

            return false;
        } catch (Exception e) {
            LOG.errorOnValidatingToken(tokenValidationEndpoint, tokenForLogging, e);
            return true;
        }
    }

    private boolean isTokenExpired(HttpUriRequestBase request, HttpClientContext context, String token) {
        try {
            return httpClient.execute(request, context, response -> {
                int code = response.getCode();
                if (code == HttpStatus.SC_UNAUTHORIZED) {
                    LOG.traceOnValidatingTokenTokenExpired(token);
                    return true;
                } else if (code == HttpStatus.SC_FORBIDDEN) {
                    LOG.traceOnValidatingTokenTokenForbidden(token);
                    return true;
                } else if (code != HttpStatus.SC_OK) {
                    LOG.errorOnValidatingTokenHttp(tokenValidationEndpoint,
                            token,
                            code,
                            trimBodyForLogging(EntityUtils.toString(response.getEntity())),
                            new HttpException("Got unexpected response code: " + code)
                    );
                    return true;
                }
                return false;
            });
        } catch (IOException e) {
            LOG.errorOnValidatingTokenIO(tokenValidationEndpoint,
                    token,
                    ConfigKeys.HTTP_CONNECTION_TIMEOUT,
                    configuration.getConnectTimeout(),
                    ConfigKeys.HTTP_SOCKET_TIMEOUT,
                    configuration.getReadTimeout(),
                    e);

            return true;
        }
    }

    private String getTokenValidating(Map<String, List<String>> requestParameters) {

        Map<String, List<String>> mergedParams = new LinkedHashMap<>();
        mergedParams.putAll(this.defaultParameters);
        mergedParams.putAll(requestParameters);

        Map<String, List<String>> paramsForLogging = clearParamsForLogging(mergedParams);

        LOG.traceGetToken(paramsForLogging);

        ClientCredentialsTokenModel token = configuration.isTokensCacheEnabled() ? tokens.get(mergedParams) : null;

        if (token == null) {
            LOG.traceNoTokenInStore(paramsForLogging);
            token = authorizeAndGetToken(mergedParams);
            if (configuration.isTokensCacheEnabled()) {
                putToken(mergedParams, token);
            }
        } else {
            String tokenForLogging = trimTokenForLogging(token.getValue());
            if (isExpired(token.getValue())) {
                LOG.traceTokenExpired(paramsForLogging, tokenForLogging);
                clearToken(mergedParams);
                token = authorizeAndGetToken(mergedParams);
                putToken(mergedParams, token);
            } else {
                LocalDateTime expirationUpdateTime = LocalDateTime.now().plusSeconds(configuration.getUpdateTimeBeforeTokenExpiration());
                if (expirationUpdateTime.isAfter(token.getExpiresIn())) {
                    LOG.traceTokenExpired(paramsForLogging, tokenForLogging);
                    clearToken(mergedParams);
                    token = authorizeAndGetToken(mergedParams);
                    putToken(mergedParams, token);
                }
                LOG.traceGotTokenFromStore(paramsForLogging, tokenForLogging);
            }
        }
        return token.getValue();
    }

    private void putToken(Map<String, List<String>> params, ClientCredentialsTokenModel token) {
        LOG.tracePutTokenInStore(clearParamsForLogging(params), trimTokenForLogging(token.getValue()));
        tokens.put(params, token);
    }

    private void clearToken(Map<String, List<String>> params) {
        LOG.traceRemovedTokenFromStore(clearParamsForLogging(params));
        tokens.remove(params);
    }

    private ClientCredentialsTokenModel authorizeAndGetToken(Map<String, List<String>> additionalRequestParameters) {
        final List<NameValuePair> params = new ArrayList<>();
        if (additionalRequestParameters != null) {
            for (Map.Entry<String, List<String>> entry : additionalRequestParameters.entrySet()) {
                entry.getValue().forEach(v -> params.add(new BasicNameValuePair(entry.getKey(), v)));
            }
        }

        List<NameValuePair> paramsForLogging = clearParamsForLogging(params);
        LOG.traceRequestNewToken(paramsForLogging);

        ClientCredentialsTokenModel model;
        try {
            HttpClientContext context = new HttpClientContext();
            HttpPost httpPost = HttpHelper.getHttpPostWithEntity(accessTokenEndpoint, params);

            model = httpClient.execute(httpPost, context, new TokenPostHandler());
        } catch (IOException e) {
            LOG.errorOnGetTokenIO(accessTokenEndpoint, paramsForLogging,
                    ConfigKeys.HTTP_CONNECTION_TIMEOUT,
                    configuration.getConnectTimeout(),
                    ConfigKeys.HTTP_SOCKET_TIMEOUT,
                    configuration.getReadTimeout(),
                    e);
            throw new AuthenticationException("Cannot get client_credentials token", e);
        } catch (Exception e) {
            LOG.errorOnGetToken(accessTokenEndpoint, paramsForLogging, e);
            throw new AuthenticationException("Cannot get client_credentials token", e);
        }

        return model;
    }

    private class TokenPostHandler implements HttpClientResponseHandler<ClientCredentialsTokenModel> {
        @Override
        public ClientCredentialsTokenModel handleResponse(ClassicHttpResponse response) throws IOException, ParseException {
            int code = response.getCode();
            if (code == HttpStatus.SC_OK) {
                String responseJson = EntityUtils.toString(response.getEntity());
                TokenResponse tokenResponse = parseTokenResponseJson(responseJson);

                final String token = tokenResponse.getAccessToken();
                LocalDateTime issueDate = LocalDateTime.now();
                return new ClientCredentialsTokenModel(
                        token,
                        issueDate,
                        issueDate.plusSeconds(tokenResponse.getExpiresIn())
                );

            } else if (is5xxServerError(code)) {
                throw new NetworkErrorException("Cannot get client_credentials token. SSO server error",
                        new HttpException("Response code: " + code + ", body: "
                                + ClientCredentialsClientImpl.this.trimBodyForLogging(EntityUtils.toString(response.getEntity())))
                );
            } else {
                throw new AuthenticationException("Cannot get client_credentials token");
            }
        }

        private boolean is5xxServerError(int code) {
            return code >= 500;
        }
    }

    private String trimTokenForLogging(String token) {
        if (token == null) {
            return "<none>";
        } else if (this.configuration.legacyMaskingEnabled()) {
            return token.substring(0, Math.min(16, token.length()));
        } else {
            return token;
        }
    }

    private String trimBodyForLogging(String body) {
        if (body == null) {
            return "<none>";
        } else {
            return body.substring(0, Math.min(200, body.length()));
        }
    }

    private Map<String, List<String>> clearParamsForLogging(Map<String, List<String>> params) {
        Map<String, List<String>> ret = new LinkedHashMap<>(params);
        if (this.configuration.legacyMaskingEnabled() && ret.containsKey("client_secret")) {
            ret.put("client_secret", Collections.singletonList("***"));
        }
        return ret;
    }

    private List<NameValuePair> clearParamsForLogging(List<NameValuePair> params) {
        List<NameValuePair> ret = new ArrayList<>();

        params.forEach(item -> {
            if (this.configuration.legacyMaskingEnabled() && item.getName().equals("client_secret")) {
                ret.add(new BasicNameValuePair("client_secret", "***"));
            } else {
                ret.add(item);
            }
        });

        return ret;
    }

    private static TokenResponse parseTokenResponseJson(String json) {
        try {
            return MAPPER.readValue(json, TokenResponse.class);
        } catch (IOException e) {
            throw new ValidateException("Failed to parse json", e);
        }
    }
}