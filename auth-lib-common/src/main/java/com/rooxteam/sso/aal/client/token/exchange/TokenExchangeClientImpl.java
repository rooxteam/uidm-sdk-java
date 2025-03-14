package com.rooxteam.sso.aal.client.token.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rooxteam.sso.aal.client.configuration.ClientConfiguration;
import com.rooxteam.sso.aal.client.model.TokenExchangeRequest;
import com.rooxteam.sso.aal.client.token.exchange.dto.TokenResponse;
import com.rooxteam.sso.aal.utils.StringUtils;
import com.rooxteam.util.HttpHelper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.rooxteam.sso.aal.AalLogger.LOG;


public class TokenExchangeClientImpl implements TokenExchangeClient {
    public static final String TOKEN_EXCHANGE_GRANT_TYPE = "urn:ietf:params:oauth:grant-type:token-exchange";
    public static final String CLIENT_ID_PARAM_NAME = "client_id";
    public static final String CLIENT_SECRET_PARAM_NAME = "client_secret";
    public static final String GRANT_TYPE_PARAM = "grant_type";
    public static final String SUBJECT_TOKEN_TYPE_PARAM = "subject_token_type";
    public static final String REQUESTED_TOKEN_TYPE_PARAM = "requested_token_type";

    public static final String SUBJECT_TOKEN_PARAM = "subject_token";
    public static final String ROLES_PARAM = "roles";
    public static final String SCOPE_PARAM = "scope";
    public static final String RESOURCE_PARAM = "resource";
    public static final String AUDIENCE_PARAM = "audience";
    public static final String REALM_PARAM = "realm";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final ClientConfiguration configuration;
    private final CloseableHttpClient closableHttpClient;
    private final boolean shouldCloseHttpClient;

    public TokenExchangeClientImpl(ClientConfiguration configuration,
                                   CloseableHttpClient closableHttpClient,
                                   boolean shouldCloseHttpClient) {
        this.configuration = configuration;
        this.closableHttpClient = closableHttpClient;
        this.shouldCloseHttpClient = shouldCloseHttpClient;
    }

    @Override
    public TokenResponse exchangeToken(TokenExchangeRequest tokenExchangeRequest,
                                       Map<String, String> extraArgs) throws Exception {
        validateRequest(tokenExchangeRequest);
        Map<String, String> map = commonParams(tokenExchangeRequest);
        map.putAll(extraArgs);
        return exchangeToken(toNameValuePairList(map));
    }

    private TokenResponse exchangeToken(List<NameValuePair> params) throws Exception {
        URI url = configuration.getTokenExchangeEndpoint();
        HttpPost post = HttpHelper.getHttpPostWithEntity(url, params);
        try (CloseableHttpResponse response = closableHttpClient.execute(post)) {
            if (success(response)) {
                byte[] json = EntityUtils.toByteArray(response.getEntity());
                return OBJECT_MAPPER.readValue(json, TokenResponse.class);
            }
        }
        return null;
    }

    private void validateRequest(TokenExchangeRequest tokenExchangeRequest) {
        if (tokenExchangeRequest.getSubjectToken() == null) {
            LOG.errorRequiredSubjectTokenMissing();
            throw new IllegalArgumentException("Required subjectToken is missing");
        }

        if (tokenExchangeRequest.getSubjectTokenType() == null) {
            LOG.errorRequiredSubjectTokenTypeMissing();
            throw new IllegalArgumentException("Required subjectTokenType is missing");
        }
    }

    private Map<String, String> commonParams(TokenExchangeRequest tokenExchangeRequest) {
        Map<String, String> map = new HashMap<>();
        map.put(GRANT_TYPE_PARAM, TOKEN_EXCHANGE_GRANT_TYPE);

        Optional.ofNullable(tokenExchangeRequest.getClientId()).ifPresent(v -> map.put(CLIENT_ID_PARAM_NAME, v));
        String clientSecret = Optional.ofNullable(tokenExchangeRequest.getClientSecret())
                .orElseGet(() ->
                        Optional.ofNullable(tokenExchangeRequest.getClientId())
                                .map(configuration::getClientSecret)
                                .orElse(null)
                );
        map.put(SUBJECT_TOKEN_TYPE_PARAM, tokenExchangeRequest.getSubjectTokenType());
        map.put(SUBJECT_TOKEN_PARAM, tokenExchangeRequest.getSubjectToken());

        Optional.ofNullable(clientSecret).ifPresent(v -> map.put(CLIENT_SECRET_PARAM_NAME, v));
        Optional.ofNullable(tokenExchangeRequest.getRoles()).ifPresent(v -> map.put(ROLES_PARAM, v));
        Optional.ofNullable(tokenExchangeRequest.getResource()).ifPresent(v -> map.put(RESOURCE_PARAM, v));
        Optional.ofNullable(tokenExchangeRequest.getScope()).ifPresent(v -> map.put(SCOPE_PARAM, v));
        Optional.ofNullable(tokenExchangeRequest.getAudience()).ifPresent(v -> map.put(AUDIENCE_PARAM, v));
        Optional.ofNullable(tokenExchangeRequest.getRealm()).ifPresent(v -> map.put(REALM_PARAM, v));
        Optional.ofNullable(tokenExchangeRequest.getRequestedTokenType()).ifPresent(v -> map.put(REQUESTED_TOKEN_TYPE_PARAM, v));

        return map;
    }

    private static List<NameValuePair> toNameValuePairList(Map<String, String> map) {
        return map.entrySet().stream()
                .filter(e -> StringUtils.isNoneBlank(e.getKey(), e.getValue()))
                .map(e -> new BasicNameValuePair(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    private static boolean success(HttpResponse r) {
        return r.getCode() == HttpStatus.SC_OK;
    }

    @Override
    public void close() throws IOException {
        if (shouldCloseHttpClient) {
            closableHttpClient.close();
        }
    }
}
